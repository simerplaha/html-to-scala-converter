package simer.html.converter

import org.scalajs.dom
import org.scalajs.dom.html.TextArea
import org.scalajs.dom.raw.{DOMParser, Document, Node}
import org.scalajs.dom.{NamedNodeMap, NodeList, html}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

sealed trait ConverterType

case object ReactScalaTagsConverter extends ConverterType

case object ScalaTagsConverter extends ConverterType

@JSExport
object HtmlToScalaTagsConverter {

  @JSExport
  def main(mainDiv: html.Div): Unit = {

    val content =
      div(
        ul(
          li(
            a(cls := "HTML TO SCALA CONVERT", href := "#")("HTML TO SCALA CONVERTER")
          )
        ),
        table(width := "100%")(
          tr(width := "100%")(
            td(width := "50%")(
              textarea(id := "htmlCode", cls := "boxsizingBorder", width := "100%", rows := 20, placeholder := "Enter your HTML code here.")(
                """<div class="myClass">
                  |    <div class="someClass" data-attribute="someValue">
                  |        <button type="button" class="btn btn-default">Button</button>
                  |    </div>
                  |    <br/>
                  |    <p>
                  |       My paragraph
                  |    </p>
                  |</div>""".stripMargin
              )
            ),
            td(width := "50%")(
              div(id := "output")(
                textarea(id := "scalaTagsCode", cls := "boxsizingBorder", width := "100%", rows := 20, placeholder := "Scala code will be generated here.")
              )
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
    if (childNodes == null)
      IndexedSeq.empty[Node]
    else
      for (i <- 0 until childNodes.length) yield childNodes.item(i)
  }


  def runConverter(converterType: ConverterType) = {
    val wrapper = "htmlCodeContainerToBeRemoved"
    val htmlCode = s"<$wrapper>" + dom.document.getElementById("htmlCode").asInstanceOf[TextArea].value + s"</$wrapper>"
    val parsedXml: Document = new DOMParser().parseFromString(htmlCode, "text/xml")
    val outputDiv = dom.document.getElementById("scalaTagsCode")
    val rootWrapperNode = parsedXml.childNodes.item(0)
    val outputString =
      if (rootWrapperNode.firstChild.nodeName == "parsererror") {
        "Parse error: \n" + rootWrapperNode.firstChild.textContent
      } else {
        val scalaCode = converterType match {
          case ReactScalaTagsConverter =>
            toScalaTags(rootWrapperNode, "^.", "<.", "reactAttr", "className")
          case ScalaTagsConverter =>
            toScalaTags(rootWrapperNode, "", "", "attr", "cls")
        }
        val wrapperMatcher = s"\\<\\.$wrapper\\(|$wrapper\\("

        scalaCode.replaceFirst(wrapperMatcher, "").dropRight(1)
      }
    outputDiv.textContent = outputString
  }

  def toScalaTags(node: Node, attributePrefix: String, nodePrefix: String, customAttributePostfix: String, classAttributeKey: String): String = {
    val attributes = {
      if (node == null || js.isUndefined(node.attributes) || node.attributes.length == 0) {
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
      .map(node => toScalaTags(node, attributePrefix, nodePrefix, customAttributePostfix, classAttributeKey))
      .mkString(",\n")

    if (node == null)
      ""
    else if (node.nodeName == "#text")
      s""""${node.nodeValue.trim}""""
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
