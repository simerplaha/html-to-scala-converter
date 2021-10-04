package simer.html.converter

import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.html.TextArea
import org.scalajs.dom.raw.{DOMParser, NamedNodeMap, Node}

import scala.scalajs.js

object HtmlToScalaTagsConverter {

  def main(args: Array[String]): Unit = {
    val template = HTMLTemplate.template(runConverter)
    dom.document.getElementById("content").appendChild(template.render)
  }

  def runConverter(converterType: ConverterType): Unit = {
    val htmlCode = dom.document.getElementById("htmlCode").asInstanceOf[TextArea].value
    val parsedHtml = new DOMParser().parseFromString(htmlCode, "text/html")
    val scalaCodeTextArea = dom.document.getElementById("scalaTagsCode").asInstanceOf[TextArea]
    val rootChildNodes = removeGarbageChildNodes(parsedHtml)
    //having more then one HTML tree causes the DOMParser to generate an incorrect tree.
    val scalaCodes = rootChildNodes.map(toScalaTags(_, converterType))
    val scalaCode =
      if (scalaCodes.size > 1) {
        val fixMe = s"""//FIXME - MULTIPLE HTML TREES PASSED TO THE CONVERTER. THIS MIGHT GENERATE UNEXPECTED CODE. Check <!DOCTYPE html> is not in the input HTML."""
        fixMe + "\n" + scalaCodes.mkString(", ")
      } else
        scalaCodes.mkString(", ")

    val scalaCodeWithoutParserAddedTags = removeTagsFromScalaCode(htmlCode, scalaCode, "html", "head", "body")
    scalaCodeTextArea.value = scalaCodeWithoutParserAddedTags.trim
  }

  def removeGarbageChildNodes(node: Node): collection.Seq[Node] =
    node.childNodes.filterNot(isGarbageNode)

  def isGarbageNode(node: Node): Boolean =
    js.isUndefined(node) || node.nodeName == "#comment" || (node.nodeName == "#text" && node.nodeValue.trim.isEmpty)

  /**
    * Recursively generates the output Scalatag's code for each HTML node and it's children.
    *
    * Filters out comments and empty text's nodes (garbage nodes :D) from the input HTML before converting to Scala.
    */
  def toScalaTags(node: Node, converterType: ConverterType): String = {
    //removes all comment and empty text nodes.
    val childrenWithoutGarbageNodes: collection.Seq[Node] = removeGarbageChildNodes(node)

    val children =
      childrenWithoutGarbageNodes
        .map(toScalaTags(_, converterType))
        .mkString(",\n")

    toScalaTag(node, converterType, childrenWithoutGarbageNodes, children)
  }

  /**
    * Converts a single HTML node, given it's child nodes's (@param children) already converted.
    */
  private def toScalaTag(node: Node,
                         converterType: ConverterType,
                         childrenWithoutGarbageNodes: collection.Seq[Node],
                         children: String): String = {

    val scalaAttrList = toScalaAttributes(attributes = node.attributes, converterType)

    node.nodeName match {
      case "#text" =>
        tripleQuote(node.nodeValue)

      case _ =>
        val nodeNameLowerCase = node.nodeName.toLowerCase
        val replacedNodeName = converterType.tagNameMap.getOrElse(nodeNameLowerCase, nodeNameLowerCase)
        val nodeString = s"${converterType.nodePrefix}$replacedNodeName"

        if (scalaAttrList.isEmpty && children.isEmpty) {
          converterType match {
            case _: LaminarConverter =>
              s"$nodeString()" //laminar requires nodes to be closed eg: br()

            case _: ReactScalaTagsConverter | _: ScalaTagsConverter =>
              nodeString
          }
        } else {
          val scalaAttrString =
            if (scalaAttrList.isEmpty)
              ""
            else if (!converterType.newLineAttributes)
              scalaAttrList.mkString("\n", ",\n", "")
            else
              scalaAttrList.mkString(", ")

          val childrenString =
            if (children.isEmpty) {
              ""
            } else {
              //text child nodes can be a part of the same List as the attribute List. They don't have to go to a new line.
              val isChildNodeATextNode = childrenWithoutGarbageNodes.headOption.exists(_.nodeName == "#text")
              val commaMayBe = if (scalaAttrString.isEmpty) "" else ","
              val startNewLineMayBe = if (isChildNodeATextNode && (converterType.newLineAttributes || scalaAttrString.isEmpty)) "" else "\n"
              //add a newLine at the end if this node has more then one child nodes
              val endNewLineMayBe = if (isChildNodeATextNode && childrenWithoutGarbageNodes.size <= 1) "" else "\n"
              s"$commaMayBe$startNewLineMayBe$children$endNewLineMayBe"
            }

          s"$nodeString($scalaAttrString$childrenString)"
        }
    }
  }

  /**
    * Converts input string of format "list-style: none; padding: 0;"
    * to ("list-style" -> "none", "padding" -> "0")
    */
  def splitAttrValueToTuples(attrValueString: String) =
    attrValueString.split(";").map {
      string =>
        val styleKeyValue = string.split(":")
        s""""${styleKeyValue.head.trim}" -> "${styleKeyValue.last.trim}""""
    }.mkString(", ")

  /**
    * Converts HTML node attributes to Scalatags attributes
    */
  def toScalaAttributes(attributes: NamedNodeMap,
                        converterType: ConverterType): Iterable[String] =
    if (js.isUndefined(attributes) || attributes.isEmpty)
      List.empty
    else
      attributes.map {
        case (attrKey, attrValue) =>
          val attrValueString = attrValue.value
          val escapedAttrValue = tripleQuote(attrValueString)
          val attributeNameMapOption = converterType.attributeNameMap.get(attrKey)

          attrKey match {
            case "style" =>
              val styleValuesDictionary =
                converterType match {
                  case _: ReactScalaTagsConverter | _: ScalaTagsConverter =>
                    s"js.Dictionary(${splitAttrValueToTuples(attrValueString)})"

                  case _: LaminarConverter =>
                    escapedAttrValue //if it's laminar then do not split. Simply return the javascript string value.
                }

              attributeNameMapOption match {
                case Some(attrNameMap) =>
                  s"""${converterType.attributePrefix}${attrNameMap.keyName} ${attrNameMap.functionName} $styleValuesDictionary"""

                case None =>
                  s"""${converterType.attributePrefix}$attrKey := $styleValuesDictionary"""
              }

            case _ =>
              attributeNameMapOption match {
                case Some(attrNameMap) =>
                  s"${converterType.attributePrefix}${attrNameMap.keyName} ${attrNameMap.functionName} $escapedAttrValue"

                case None =>
                  if (attrKey.matches("[a-zA-Z0-9]*$"))
                    s"${converterType.attributePrefix}$attrKey := $escapedAttrValue"
                  else //else it's a custom attribute
                    s"""${converterType.customAttributeFunctionName}("$attrKey") := $escapedAttrValue"""
              }
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
