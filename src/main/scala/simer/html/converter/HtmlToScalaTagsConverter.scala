package simer.html.converter

import org.scalajs.dom
import org.scalajs.dom.html.TextArea
import org.scalajs.dom.raw.{DOMParser, Document, Node}
import org.scalajs.dom.{NamedNodeMap, NodeList, html}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object HtmlToScalaTagsConverter {

  val content =
    div(
      ul(
        li(
          a(href := "#")("HTML TO SCALATAGS CONVERTER")
        )
      ),
      table(width := "100%")(
        tr(width := "100%")(
          th(width := "50%")(
            h4("HTML")
          ),
          th(width := "50%")(
            h4("Scalatags")
          )
        ),
        tr(width := "100%")(
          td(width := "50%")(
            textarea(id := "htmlCode", cls := "boxsizingBorder", width := "100%", rows := 26, placeholder := "Enter your HTML code here.")(
              """<div class="myClass">
                |    <div class="someClass" data-attribute="someValue">
                |        <button type="button" class="btn btn-default">Button</button>
                |    </div>
                |    <br/>
                |    <span>
                |       <img class="my-img-class" src="assets/images/image1.jpg" onclick='alert("clicked!");' alt=""/>
                |    </span>
                |    <a href="javascript:void(0);" class="my-class" data-toggle="dropdown">
                |       Some link
                |    </a>
                |    <ul class="dropdown-menu">
                |       <li>
                |           List item 1
                |       </li>
                |       <li>
                |           List&nbsp;item&nbsp;2
                |       </li>
                |    </ul>
                |    <script>
                |       document.getElementById("someId").value = "Hello Scala.js!";
                |    </script>
                |</div>""".stripMargin
            )
          ),
          td(width := "50%")(
            textarea(id := "scalaTagsCode", cls := "boxsizingBorder", width := "100%", rows := 26, placeholder := "Scala code will be generated here.")
          )
        ),
        tr(width := "100%")(
          td(colspan := "2", textAlign := "center")(
            button(cls := "myButton", onclick := { () => runConverter(ReactScalaTagsConverter) })("Convert to Scalajs-React's Scalatags"),
            span("  "),
            button(cls := "myButton", onclick := { () => runConverter(ScalaTagsConverter) })("Convert to Scalatags")
          )
        )
      )
    )

  @JSExport
  def main(mainDiv: html.Div): Unit =
    mainDiv.appendChild(content.render)


  implicit def asAttrMap(nodeMap: NamedNodeMap): IndexedSeq[(String, String)] = {
    for (i <- 0 until nodeMap.length)
      yield (nodeMap.item(i).name, nodeMap.item(i).value)
  }

  implicit def childNodes(childNodes: NodeList): IndexedSeq[Node] = {
    if (js.isUndefined(childNodes))
      IndexedSeq.empty[Node]
    else
      for (i <- 0 until childNodes.length) yield childNodes.item(i)
  }

  def runConverter(converterType: ConverterType) = {
    val htmlCode = dom.document.getElementById("htmlCode").asInstanceOf[TextArea].value
    val parsedHtml = new DOMParser().parseFromString(htmlCode, "text/html")
    val scalaCodeTextArea = dom.document.getElementById("scalaTagsCode").asInstanceOf[TextArea]
    val htmlTagNode = parsedHtml.childNodes.item(0)
    val outputScalaTagsCode = toScalaTags(htmlTagNode, converterType)
    val outputScalaTagsCodeRemovedParserAddedTags = removeParserAddedTags(htmlCode, outputScalaTagsCode)
    scalaCodeTextArea.value = outputScalaTagsCodeRemovedParserAddedTags.trim
  }

  def toScalaTags(node: Node, converterType: ConverterType): String = {

    val attributePrefix = converterType.attributePrefix
    val classAttributeKey = converterType.classAttributeKey
    val customAttributePostfix = converterType.customAttributePostfix
    val nodePrefix = converterType.nodePrefix

    val attributes = {
      if (js.isUndefined(node) || js.isUndefined(node.attributes) || node.attributes.length == 0) {
        ""
      } else {
        node.attributes.map {
          case (key, value) =>
            val escapedValue = tripleQuoteString(value)
            if (key == "class")
              s"${attributePrefix + classAttributeKey + ":=" + escapedValue}"
            else if (key == "for" || key == "type")
              s"$attributePrefix`$key` := $escapedValue"
            else if (!key.matches("[a-zA-Z0-9]*$"))
              s""""$key".$customAttributePostfix := $escapedValue"""
            else
              s"$attributePrefix$key := $escapedValue"
        }.mkString(", ")
      }
    }

    val children = node.childNodes
      .filterNot(node => node.nodeName == "#comment" || (node.nodeName == "#text" && node.nodeValue.trim.isEmpty))
      .map(toScalaTags(_, converterType))
      .mkString(",\n")

    if (js.isUndefined(node))
      ""
    else if (node.nodeName == "#text")
      tripleQuoteString(node.nodeValue)
    else if(attributes.isEmpty && children.isEmpty)
      s"${nodePrefix + node.nodeName.toLowerCase}"
    else {
      s"${nodePrefix + node.nodeName.toLowerCase}($attributes${
        if (children.isEmpty)
          children
        else if (attributes.isEmpty)
          "\n" + children + "\n"
        else
          ",\n" + children + "\n"
      })"
    }
  }

  /**
    * The html parser seems to add html, head and body tags the parsed tree. This code will remove the ones
    */
  def removeParserAddedTags(htmlCode: String, scalaCode: String): String = {
    val removeHtmlTag = "(?i)<html".r.findFirstMatchIn(htmlCode).isEmpty
    val removeHeadTag = "(?i)<head".r.findFirstMatchIn(htmlCode).isEmpty
    val removeBodyTag = "(?i)<body".r.findFirstMatchIn(htmlCode).isEmpty

    Map("html" -> removeHtmlTag, "head" -> removeHeadTag, "body" -> removeBodyTag).foldLeft(scalaCode) {
      case (outputString, (tagName, toRemove)) =>
        toRemove match {
          case true =>
            val removedTagString = outputString.replaceFirst(s".*$tagName.+", "").trim
            if (tagName != "head") {
              removedTagString.dropRight(1)
            } else
              removedTagString
          case _ =>
            outputString
        }
    }
  }

  def tripleQuoteString(string: String): String = {
    string.trim match {
      case string if string.contains("\"") || string.contains("\n") || string.contains("\\") =>
        s"""\"\"\"$string\"\"\""""
      case string =>
        s""""$string""""
    }
  }
}
