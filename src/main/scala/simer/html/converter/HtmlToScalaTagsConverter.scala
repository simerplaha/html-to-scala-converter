package simer.html.converter

import org.scalajs.dom
import org.scalajs.dom.html.TextArea
import org.scalajs.dom.raw.{DOMParser, Node}
import org.scalajs.dom.{NamedNodeMap, NodeList, html}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object HtmlToScalaTagsConverter {

  @JSExport
  def main(mainDiv: html.Div): Unit = {

    val content =
      div(
        ul(
          li(
            a(cls := "HTML TO SCALATAGS CONVERT", href := "#")("HTML TO SCALATAGS CONVERTER")
          )
        ),
        table(width := "100%")(
          tr(width := "100%")(
            th(width := "50%")(
              h4("HTML")
            ),
            th(width := "50%")(
              h4("Scala")
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
                  |       <img class="my-img-class" src="assets/images/image1.jpg" alt=""/>
                  |    </span>
                  |    <a href="javascript:void(0);" class="my-class" data-toggle="dropdown">
                  |       Some link
                  |    </a>
                  |    <ul class="dropdown-menu">
                  |       <li>
                  |           <a href="home.html">Home</a>
                  |       </li>
                  |       <li>
                  |           <a href="contact.html">Contact</a>
                  |       </li>
                  |    </ul>
                  |    <script>
                  |       document.getElementById("color:mediumblue">"demo").innerHTML = "color:mediumblue">"Hello Scala.js!";
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
    mainDiv.appendChild(content.render)
  }


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
    val wrapper = "tempHtmlCodeWrapper" //helpful when the input HTML does not have a root container node.
    val htmlCodeString = s"<$wrapper>" + dom.document.getElementById("htmlCode").asInstanceOf[TextArea].value + s"</$wrapper>"
    val parsedXml = new DOMParser().parseFromString(htmlCodeString, "text/xml")
    val scalaCodeTextArea = dom.document.getElementById("scalaTagsCode").asInstanceOf[TextArea]
    val rootWrapperNode = parsedXml.childNodes.item(0)
    val outputScalaTagsCode =
      rootWrapperNode.firstChild match {
        case firstChild if js.isUndefined(rootWrapperNode.firstChild) =>
          ""
        case firstChild if rootWrapperNode.firstChild.nodeName == "parsererror" =>
          "Parse error: \n" + firstChild.textContent
        case _ =>
          val scalaCode = toScalaTags(rootWrapperNode, converterType)
          val wrapperMatcher = s"\\<\\.$wrapper\\(|$wrapper\\("
          scalaCode.replaceFirst(wrapperMatcher, "").dropRight(1)
      }
    scalaCodeTextArea.value = outputScalaTagsCode
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
            if (key == "class")
              s"${attributePrefix + classAttributeKey} := ${s""""$value""""}"
            else if (key == "for" || key == "type")
              s"$attributePrefix`$key` := ${s""""$value""""}"
            else if (key.contains("-"))
              s""""$key".$customAttributePostfix := ${s""""$value""""}"""
            else
              s"$attributePrefix$key := ${s""""$value""""}"
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
      node.nodeValue.trim match {
        case nodeValue if node.nodeValue.trim.contains("\"") =>
          s"""\"\"\"$nodeValue\"\"\""""
        case nodeValue =>
          s""""$nodeValue""""
      }
    else {
      s"${nodePrefix + node.nodeName}($attributes${
        if (children.isEmpty)
          children
        else if (attributes.isEmpty)
          children + "\n"
        else
          ")(\n" + children + "\n"
      })"
    }
  }
}
