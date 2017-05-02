package simer.html.converter

import org.scalajs.dom
import org.scalajs.dom.html.{Input, TextArea}
import org.scalajs.dom.raw.{DOMParser, Node}
import org.scalajs.dom.{NamedNodeMap, NodeList}
import scala.scalajs.js.JSApp
import scala.scalajs.js
import scalatags.JsDom.all._

object HtmlToScalaTagsConverter extends JSApp {

  def main(): Unit =
    dom.document.getElementById("content").appendChild(content.render)

  val content =
    div(
      ul(
        li(
          a(href := "#")("HTML TO SCALATAGS CONVERTER")
        ),
        li(float := "right",
          u(
            a(href := "https://github.com/simerplaha/html-to-scalatags-converter/issues", target := "blank")("Report an issue")
          )
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
                |    <ul class="dropdown-menu" style="list-style: none; padding: 0;">
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
            span("Add attributes on new line: "),
            input(id := "newlineAttributes", cls := "myButton", `type` := "checkbox")
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


  implicit def asAttrMap(nodeMap: NamedNodeMap): IndexedSeq[(String, String)] =
    for (i <- 0 until nodeMap.length) yield (nodeMap.item(i).name, nodeMap.item(i).value)

  implicit def childNodes(childNodes: NodeList): IndexedSeq[Node] =
    if (js.isUndefined(childNodes))
      IndexedSeq.empty[Node]
    else
      for (i <- 0 until childNodes.length) yield childNodes.item(i)

  def runConverter(converterType: ConverterType) = {
    val htmlCode = dom.document.getElementById("htmlCode").asInstanceOf[TextArea].value
    val parsedHtml = new DOMParser().parseFromString(htmlCode, "text/html")
    val scalaCodeTextArea = dom.document.getElementById("scalaTagsCode").asInstanceOf[TextArea]
    val newlineAttributes = dom.document.getElementById("newlineAttributes").asInstanceOf[Input]
    val htmlTagNode = parsedHtml.childNodes.item(0)
    val outputScalaTagsCode = toScalaTags(htmlTagNode, converterType, !newlineAttributes.checked)
    val outputScalaTagsCodeRemovedParserAddedTags = removeParserAddedTags(htmlCode, outputScalaTagsCode)
    scalaCodeTextArea.value = outputScalaTagsCodeRemovedParserAddedTags.trim
  }

  /**
    * Recursively generates the output Scalatag's code.
    */
  def toScalaTags(node: Node, converterType: ConverterType, inlineAttributes: Boolean): String = {
    //gets rid of all comments and text node which are empty
    val childrenWithoutGarbageNodes = node.childNodes
      .filterNot(node => node.nodeName == "#comment" || (node.nodeName == "#text" && node.nodeValue.trim.isEmpty))

    val children = childrenWithoutGarbageNodes
      .map(toScalaTags(_, converterType, inlineAttributes))
      .mkString(",\n")

    val attributes = buildAttributeString(node, inlineAttributes, converterType.attributePrefix, converterType.classAttributeKey, converterType.customAttributePostfix)
    val nodePrefix = converterType.nodePrefix

    //text child nodes can be a part of the same List as the attribute List. They don't have to go to a new line.
    val isChildNodeATextNode = childrenWithoutGarbageNodes.nonEmpty && childrenWithoutGarbageNodes.head.nodeName == "#text"

    if (node.nodeName == "#text")
      tripleQuoteString(node.nodeValue)
    else if (attributes.isEmpty && children.isEmpty)
      s"${nodePrefix + node.nodeName.toLowerCase}"
    else {
      s"${nodePrefix + node.nodeName.toLowerCase}($attributes${
        if (children.isEmpty)
          ""
        else {
          val commaMayBe = if (attributes.isEmpty) "" else ","
          val startNewLineMayBe = if (isChildNodeATextNode && (inlineAttributes || attributes.isEmpty)) "" else "\n"
          //add a newLine at the end if this node has more then one child nodes
          val endNewLineMayBe = if (isChildNodeATextNode && childrenWithoutGarbageNodes.size <= 1) "" else "\n"
          s"$commaMayBe$startNewLineMayBe$children$endNewLineMayBe"
        }
      })"
    }
  }

  def buildAttributeString(node: Node, inlineAttributes: Boolean, attributePrefix: String, classAttributeKey: String, customAttributePostfix: String): String =
    if (js.isUndefined(node) || js.isUndefined(node.attributes) || node.attributes.length == 0) {
      ""
    } else {
      val attributesMap =
        node.attributes.map {
          case (key, value) =>
            val escapedValue = tripleQuoteString(value)
            if (key == "class")
              s"${attributePrefix + classAttributeKey + " := " + escapedValue}"
            else if (key == "style") {
              val attributeKeyAndValue = value.split(";")
              val dictionaryStrings = attributeKeyAndValue.map {
                string =>
                  val styleKeyValue = string.split(":")
                  s""""${styleKeyValue.head.trim}" -> "${styleKeyValue.last.trim}""""
              }.mkString(", ")
             s"""${attributePrefix + key} := js.Dictionary($dictionaryStrings)"""
            }
            else if (key == "for" || key == "type")
              s"$attributePrefix`$key` := $escapedValue"
            else if (!key.matches("[a-zA-Z0-9]*$"))
              s"""$customAttributePostfix("$key") := $escapedValue"""
            else
              s"$attributePrefix$key := $escapedValue"
        }
      if (!inlineAttributes)
        attributesMap.mkString("\n", ",\n", "")
      else
        attributesMap.mkString(", ")
    }

  /**
    * The javascript html parser seems to add html, head and body tags the parsed tree by default.
    * This code will remove the ones that are not in the input HTML.
    *
    * TODO: Do this in the javascript parsed tree before generating scala code instead of operating on the
    * scala generated code.
    * (Will leave this as is for now because it's easier this way then working with Javascript's untyped API)
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
