package simer.html.converter.tyrian

import org.scalajs.dom.raw.Node
import simer.html.converter.ConverterType
import simer.html.converter.utils._

import scala.scalajs.js
import org.scalajs.dom.ext._
import simer.html.converter.tyrian.TyrianAttributes.{NoValue, Normal}
import simer.html.converter.tyrian.TyrianTags.{NoChildren, OptionalChildren}

object TyrianConverter {
  def toScala(node: Node,
              converterType: ConverterType,
              childrenWithoutGarbageNodes: collection.Seq[Node],
              children: String): String = {

    node.nodeName match {
      case "#text" =>
        tripleQuote(node.nodeValue)
      case _ =>

        val attrString = if (js.isUndefined(node.attributes) || node.attributes.isEmpty) {
          ""
        } else {
          val (separatorStart, separator, separatorEnd) = if (!converterType.newLineAttributes) {
            ("(\n", ",\n", "\n)")
          } else {
            ("(", ", ", ")")
          }
          node.attributes.map { case (attributeKey, attributeValue) =>
            val attributeType = TyrianAttributes.attrs.find(a => a.attrName.getOrElse(a.name) == attributeKey)

            attributeType match {
              case Some(attr: NoValue) if !converterType.isBooleanTypeConversionDisabled => attr.name
              case Some(attr: Normal) => s"${attr.name} := ${tripleQuote(attributeValue.value)}"
              case _ => s"Attribute(\"$attributeKey\", ${tripleQuote(attributeValue.value)})"
            }
          }.mkString(separatorStart, separator, separatorEnd)

        }

        val tagType = TyrianTags.tags.find(t => t.tag.getOrElse(t.name) == node.nodeName.toLowerCase)

        val innerString = tagType match {
          case Some(_: NoChildren) | Some(_: OptionalChildren) => ""
          case _ if childrenWithoutGarbageNodes.nonEmpty => s"(\n$children)"
          case _ => "()"
        }

        tagType match {
          case Some(tag) => s"${tag.name}$attrString$innerString"
          case None => s"tag(\"${node.nodeName.toLowerCase}\")$attrString$innerString"
        }
    }
  }
}
