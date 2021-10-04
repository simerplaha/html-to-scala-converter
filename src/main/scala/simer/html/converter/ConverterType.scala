package simer.html.converter

sealed trait ConverterType {
  val attributePrefix: String
  val nodePrefix: String
  val customAttributePostfix: String
  val classAttributeKey: String
  val newLineAttributes: Boolean
}

case class ReactScalaTagsConverter(newLineAttributes: Boolean) extends ConverterType {
  val attributePrefix: String = "^."
  val nodePrefix: String = "<."
  val customAttributePostfix: String = "VdomAttr"
  val classAttributeKey: String = "className"
}

case class ScalaTagsConverter(newLineAttributes: Boolean) extends ConverterType {
  val attributePrefix: String = ""
  val nodePrefix: String = ""
  val customAttributePostfix: String = "attr"
  val classAttributeKey: String = "cls"
}

case class LaminarConverter(newLineAttributes: Boolean) extends ConverterType {
  val attributePrefix: String = ""
  val nodePrefix: String = ""
  val customAttributePostfix: String = "dataAttr"
  val classAttributeKey: String = "cls"
}
