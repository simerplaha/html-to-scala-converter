package simer.html.converter


sealed trait ConverterType {
  val attributePrefix: String
  val nodePrefix: String
  val customAttributePostfix: String
  val classAttributeKey: String
}

case object ReactScalaTagsConverter extends ConverterType {
  val attributePrefix: String = "^."
  val nodePrefix: String = "<."
  val customAttributePostfix: String = "VdomAttr"
  val classAttributeKey: String = "className"
}

case object ScalaTagsConverter extends ConverterType {
  val attributePrefix: String = ""
  val nodePrefix: String = ""
  val customAttributePostfix: String = "attr"
  val classAttributeKey: String = "cls"
}
