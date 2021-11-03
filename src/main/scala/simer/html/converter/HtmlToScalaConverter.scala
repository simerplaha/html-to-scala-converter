package simer.html.converter

import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.html.TextArea
import org.scalajs.dom.raw.{DOMParser, NamedNodeMap, Node}
import simer.html.converter.ConverterType.{Laminar, ScalaJSReact, ScalaTags}

import scala.scalajs.js
import scala.util.Try

object HtmlToScalaConverter {

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
        //fetch the node/tag name supplied by the ConverterType
        val replacedNodeName = converterType.tagNameMap.getOrElse(nodeNameLowerCase, nodeNameLowerCase)
        val nodeString = s"${converterType.nodePrefix}$replacedNodeName"

        if (scalaAttrList.isEmpty && children.isEmpty) {
          converterType match {
            case _: Laminar =>
              s"$nodeString()" //laminar requires nodes to be closed eg: br()

            case _: ScalaJSReact | _: ScalaTags =>
              nodeString
          }
        } else { //this node/tag has attributes or has children
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
      attributes map {
        case (attrKey, attrValue) =>
          val attrValueString = attrValue.value
          val escapedAttrValue = tripleQuote(attrValueString)
          val attributeNameMapOption = converterType.attributeNameMap.get(attrKey)

          if (attrKey == "style") {
            val styleValuesDictionary =
              converterType match {
                case _: ScalaJSReact | _: ScalaTags =>
                  //if scalatags or scalajs-react convert the style value to dictionary
                  s"js.Dictionary(${splitAttrValueToTuples(attrValueString)})"

                case _: Laminar =>
                  //for laminar do not split. Simply return the javascript string value.
                  escapedAttrValue
              }

            attributeNameMapOption match {
              case Some(attrNameMap) =>
                s"""${converterType.attributePrefix}${attrNameMap.key} ${attrNameMap.function} $styleValuesDictionary"""

              case None =>
                s"""${converterType.attributePrefix}$attrKey := $styleValuesDictionary"""
            }
          } else {
            attributeNameMapOption match {
              case Some(attrNameMap) =>

                val typedValue =
                  attrNameMap match {
                    case _: AttributeType.StringAttribute =>
                      escapedAttrValue

                    case _: AttributeType.IntAttribute =>
                      //if unable to convert to int or else revert back to string
                      Try(attrValueString.toInt) getOrElse escapedAttrValue

                    case _: AttributeType.DoubleAttribute =>
                      //if unable to convert to double or else revert back to string
                      Try(attrValueString.toDouble) getOrElse escapedAttrValue

                    case _: AttributeType.BooleanAttribute =>
                      //if unable to convert to boolean or else revert back to string
                      //Note: In HTML the value of boolean attribute can be set to anything to enable it.
                      //      This should be looked into manually to handle cases where there could be some
                      //      javascript code dependant on the actual value.
                      Try(attrValueString.toBoolean) getOrElse escapedAttrValue

                    case _: AttributeType.EventAttribute =>
                      //Event attributes get converted to however the target library's expected syntax.
                      //https://github.com/simerplaha/html-to-scala-converter/issues/9
                      converterType match {
                        case _: ScalaJSReact =>
                          s"""Callback(js.eval($escapedAttrValue))"""

                        case _: ScalaTags =>
                          escapedAttrValue

                        case _: Laminar =>
                          s"""(_ => js.eval($escapedAttrValue))"""
                      }
                  }

                s"${converterType.attributePrefix}${attrNameMap.key} ${attrNameMap.function} $typedValue"

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
