package simer.html.converter

import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.html.{Input, TextArea}
import org.scalajs.dom.raw.{DOMParser, NamedNodeMap, Node}

import scala.scalajs.js
import scala.scalajs.js.JSApp

object HtmlToScalaTagsConverter extends JSApp {

  def main(): Unit = {
    val template = HTMLTemplate.template(runConverter)
    dom.document.getElementById("content").appendChild(template.render)
  }

  def runConverter(converterType: ConverterType): Unit = {
    val htmlCode = dom.document.getElementById("htmlCode").asInstanceOf[TextArea].value
    val parsedHtml = new DOMParser().parseFromString(htmlCode, "text/html")
    val scalaCodeTextArea = dom.document.getElementById("scalaTagsCode").asInstanceOf[TextArea]
    val addPropertiesToNewLineCheckbox = dom.document.getElementById("newlineAttributes").asInstanceOf[Input]
    val scalaCodes = parsedHtml.childNodes.map(toScalaTags(_, converterType, !addPropertiesToNewLineCheckbox.checked))
    val scalaCode =
      if (scalaCodes.size > 1) {
        val fixMe = s"""//FIXME - MULTIPLE HTML TREES PASSED TO THE CONVERTER. THIS MIGHT GENERATE UNEXPECTED SCALATAGS CODE."""
        fixMe + "\n" + scalaCodes.mkString(", ")
      } else
        scalaCodes.mkString(", ")

    val scalaCodeWithoutParserAddedTags = removeTagsFromScalaCode(htmlCode, scalaCode, "html", "head", "body")
    scalaCodeTextArea.value = scalaCodeWithoutParserAddedTags.trim
  }

  def removeGarbageChildNodes(node: Node): Seq[Node] =
    node.childNodes
      .filterNot(node => node.nodeName == "#comment" || node.nodeName == "#document" || (node.nodeName == "#text" && node.nodeValue.trim.isEmpty))

  /**
    * Recursively generates the output Scalatag's code.
    */
  def toScalaTags(node: Node, converterType: ConverterType, inlineAttributes: Boolean): String = {
    //gets rid of all comments and text node which are empty
    val childrenWithoutGarbageNodes: Seq[Node] = removeGarbageChildNodes(node)

    val children = childrenWithoutGarbageNodes
      .map(toScalaTags(_, converterType, inlineAttributes))
      .mkString(",\n")

    //convert html node attributes/properties to scala attributes/properties
    val scalaAttrString =
      if (js.isUndefined(node) || js.isUndefined(node.attributes) || node.attributes.length == 0) //node has no attributes
        ""
      else {
        val scalaAttrList =
          toScalaAttributes(
            nodeAttributes = node.attributes,
            inlineAttributes = inlineAttributes,
            attributePrefix = converterType.attributePrefix,
            classAttributeKey = converterType.classAttributeKey,
            customAttributePostfix = converterType.customAttributePostfix
          )
        if (!inlineAttributes) scalaAttrList.mkString("\n", ",\n", "") else scalaAttrList.mkString(", ")
      }

    //text child nodes can be a part of the same List as the attribute List. They don't have to go to a new line.
    val isChildNodeATextNode = childrenWithoutGarbageNodes.nonEmpty && childrenWithoutGarbageNodes.head.nodeName == "#text"

    node.nodeName match {
      case "#text" =>
        tripleQuote(node.nodeValue)

      case _ if scalaAttrString.isEmpty && children.isEmpty =>
        s"${converterType.nodePrefix + node.nodeName.toLowerCase}"

      case _ =>
        s"${converterType.nodePrefix + node.nodeName.toLowerCase}($scalaAttrString${
          if (children.isEmpty)
            ""
          else {
            val commaMayBe = if (scalaAttrString.isEmpty) "" else ","
            val startNewLineMayBe = if (isChildNodeATextNode && (inlineAttributes || scalaAttrString.isEmpty)) "" else "\n"
            //add a newLine at the end if this node has more then one child nodes
            val endNewLineMayBe = if (isChildNodeATextNode && childrenWithoutGarbageNodes.size <= 1) "" else "\n"
            s"$commaMayBe$startNewLineMayBe$children$endNewLineMayBe"
          }
        })"

    }
  }

  /**
    * Converts HTML node attributes to Scalatags attributes
    */
  def toScalaAttributes(nodeAttributes: NamedNodeMap,
                        inlineAttributes: Boolean,
                        attributePrefix: String,
                        classAttributeKey: String,
                        customAttributePostfix: String): Iterable[String] =
    nodeAttributes.map {
      case (attrKey, attrValue) =>
        val attrValueString = attrValue.value
        val escapedValue = tripleQuote(attrValueString)
        attrKey match {
          case "class" =>
            s"${attributePrefix + classAttributeKey + " := " + escapedValue}"

          case "style" =>
            val attributeKeyAndValue = attrValueString.split(";")
            val dictionaryStrings = attributeKeyAndValue.map {
              string =>
                val styleKeyValue = string.split(":")
                s""""${styleKeyValue.head.trim}" -> "${styleKeyValue.last.trim}""""
            }.mkString(", ")

            s"""${attributePrefix + attrKey} := js.Dictionary($dictionaryStrings)"""

          case "for" | "type" =>
            s"$attributePrefix`$attrKey` := $escapedValue"

          case _ if !attrKey.matches("[a-zA-Z0-9]*$") =>
            s"""$customAttributePostfix("$attrKey") := $escapedValue"""

          case _ =>
            s"$attributePrefix$attrKey := $escapedValue"
        }
    }

  /**
    * Javascript html parser seems to add <html>, <head> and <body> tags the parsed tree by default.
    * This remove the ones that are not in the input HTML.
    *
    * Might not work for tags other then html, head and body. Have not looked into others, didn't need them so far.
    */
  def removeTagsFromScalaCode(htmlCode: String, scalaCode: String, tagsToRemove: String*): String =
    tagsToRemove.foldLeft(scalaCode) {
      case (newScalaCode, tagToRemove) =>
        s"(?i)<$tagToRemove".r.findFirstMatchIn(htmlCode) match {
          case None =>
            val scalaCodeWithoutTag = newScalaCode.replaceFirst(s".*$tagToRemove.+", "").trim
            if (tagToRemove == "head") //If head if head is empty in html. Result Scalatag would be head() in Scala.
              scalaCodeWithoutTag
            else
              scalaCodeWithoutTag.dropRight(1) //remove the closing ')' for the tag if it's not head.

          case Some(_) =>
            newScalaCode
        }
    }

  def tripleQuote(string: String): String =
    string.trim match {
      case string if string.contains("\"") || string.contains("\n") || string.contains("\\") =>
        s"""\"\"\"$string\"\"\""""
      case string =>
        s""""$string""""
    }
}
