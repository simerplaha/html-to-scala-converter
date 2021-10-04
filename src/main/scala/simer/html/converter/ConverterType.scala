package simer.html.converter

/**
  * Defines mapping for HTML attribute key names to target Scala's key and function name
  *
  * For example to convert `onclick = "alert("");"` to `onClick <-- "alert("");"`
  * this map entry should be
  * {{{
  *   map.put("onclick", ScalaAttrNameMap("onClick", "<--"))
  * }}}
  *
  * @see [[LaminarConverter.attributeNameMap]]
  * @param keyName      name of the key in Scala
  * @param functionName name of function in Scala. Defaults to :=
  */

case class ScalaAttributeNameMap(keyName: String,
                                 functionName: String = ":=")

sealed trait ConverterType {
  val attributePrefix: String
  val nodePrefix: String
  val customAttributeFunctionName: String
  val newLineAttributes: Boolean
  //stores name conversions for html attribute names to scala names
  val attributeNameMap: Map[String, ScalaAttributeNameMap]
  val tagNameMap: Map[String, String]
}

case class ReactScalaTagsConverter(newLineAttributes: Boolean) extends ConverterType {
  val attributePrefix: String = "^."
  val nodePrefix: String = "<."
  val customAttributeFunctionName: String = "VdomAttr"

  override val tagNameMap: Map[String, String] =
    Map.empty

  override val attributeNameMap: Map[String, ScalaAttributeNameMap] =
    Map(
      "class" -> ScalaAttributeNameMap("className"),
      "for" -> ScalaAttributeNameMap("`for`"),
      "type" -> ScalaAttributeNameMap("`type`")
    )
}

case class ScalaTagsConverter(newLineAttributes: Boolean) extends ConverterType {
  val attributePrefix: String = ""
  val nodePrefix: String = ""
  val customAttributeFunctionName: String = "attr"

  override val tagNameMap: Map[String, String] =
    Map.empty

  override val attributeNameMap: Map[String, ScalaAttributeNameMap] =
    Map(
      "class" -> ScalaAttributeNameMap("cls"),
      "for" -> ScalaAttributeNameMap("`for`"),
      "type" -> ScalaAttributeNameMap("`type`")
    )
}

case class LaminarConverter(newLineAttributes: Boolean) extends ConverterType {
  val attributePrefix: String = ""
  val nodePrefix: String = ""
  val customAttributeFunctionName: String = "dataAttr"

  override val tagNameMap: Map[String, String] =
    Map(
      "style" -> "styleTag",
      "link" -> "linkTag",
      "param" -> "paramTag",
      "map" -> "mapTag",
      "title" -> "titleTag",
      "object" -> "objectTag"
    )

  override val attributeNameMap: Map[String, ScalaAttributeNameMap] =
    Map(
      "class" -> ScalaAttributeNameMap("cls"),
      "for" -> ScalaAttributeNameMap("forId"),
      "type" -> ScalaAttributeNameMap("tpe"),
      "value" -> ScalaAttributeNameMap("defaultValue"),
      "checked" -> ScalaAttributeNameMap("defaultChecked"),
      "selected" -> ScalaAttributeNameMap("defaultSelected"),
      "for" -> ScalaAttributeNameMap("forId"),
      "id" -> ScalaAttributeNameMap("idAttr"),
      "max" -> ScalaAttributeNameMap("maxAttr"),
      "min" -> ScalaAttributeNameMap("minAttr"),
      "step" -> ScalaAttributeNameMap("stepAttr"),
      "offset" -> ScalaAttributeNameMap("offsetAttr"),
      "result" -> ScalaAttributeNameMap("resultAttr"),
      "loading" -> ScalaAttributeNameMap("loadingAttr"),
      "style" -> ScalaAttributeNameMap("styleAttr"),
      "content" -> ScalaAttributeNameMap("contentAttr"),
      "form" -> ScalaAttributeNameMap("formId"),
      "height" -> ScalaAttributeNameMap("heightAttr"),
      "width" -> ScalaAttributeNameMap("widthAttr"),
      "list" -> ScalaAttributeNameMap("listId"),
      "contextmenu" -> ScalaAttributeNameMap("contextMenuId"),
      "onclick" -> ScalaAttributeNameMap("onClick", "-->")
    )
}
