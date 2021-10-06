package simer.html.converter

import simer.html.converter.AttributeType.{BooleanAttribute, EventAttribute, IntAttribute, StringAttribute}

sealed trait ConverterType {
  val attributePrefix: String
  val nodePrefix: String
  val customAttributeFunctionName: String
  val newLineAttributes: Boolean
  //stores name conversions for html attribute names to scala names
  val attributeNameMap: Map[String, AttributeType]
  val tagNameMap: Map[String, String]
}

object ConverterType {

  object ScalaJSReact {
    //attributes naming and type convention found in ScalaJS-React
    val dataAttributes =
      Map(
        "class" -> StringAttribute("className"),
        "for" -> StringAttribute("`for`"),
        "type" -> StringAttribute("`type`"),
        "content" -> StringAttribute("contentAttr"),
        "hreflang" -> StringAttribute("hrefLang"),
        "tabindex" -> IntAttribute("tabIndex"),
        "styleTag" -> StringAttribute("styleTag"),
        "autocomplete" -> StringAttribute("autoComplete"),
        "allowfullscreen" -> BooleanAttribute("allowFullScreen"),
        "allowtransparency" -> BooleanAttribute("allowTransparency"),
        "async" -> BooleanAttribute("async"),
        "autocorrect" -> BooleanAttribute("autoCorrect"),
        "autofocus" -> BooleanAttribute("autoFocus"),
        "autoplay" -> BooleanAttribute("autoPlay"),
        "autosave" -> BooleanAttribute("autoSave"),
        "checked" -> BooleanAttribute("checked"),
        "colspan" -> IntAttribute("colSpan"),
        "controls" -> BooleanAttribute("controls"),
        "default" -> BooleanAttribute("default"),
        "defer" -> BooleanAttribute("defer"),
        "disablepictureinpicture" -> BooleanAttribute("disablePictureInPicture"),
        "disabled" -> BooleanAttribute("disabled"),
        "draggable" -> BooleanAttribute("draggable"),
        "formnovalidate" -> BooleanAttribute("formNoValidate"),
        "hidden" -> BooleanAttribute("hidden"),
        "itemscope" -> BooleanAttribute("itemScope"),
        "loop" -> BooleanAttribute("loop"),
        "maxlength" -> IntAttribute("maxLength"),
        "minlength" -> IntAttribute("minLength"),
        "multiple" -> BooleanAttribute("multiple"),
        "muted" -> BooleanAttribute("muted"),
        "nomodule" -> BooleanAttribute("noModule"),
        "novalidate" -> BooleanAttribute("noValidate"),
        "open" -> BooleanAttribute("open"),
        "playsinline" -> BooleanAttribute("playsInline"),
        "radiogroup" -> StringAttribute("radioGroup"),
        "readonly" -> BooleanAttribute("readOnly"),
        "required" -> BooleanAttribute("required"),
        "reversed" -> BooleanAttribute("reversed"),
        "rowspan" -> IntAttribute("rowSpan"),
        "rows" -> IntAttribute("rows"),
        "scoped" -> BooleanAttribute("scoped"),
        "seamless" -> BooleanAttribute("seamless"),
        "selected" -> BooleanAttribute("selected"),
        "size" -> IntAttribute("size"),
        "spellcheck" -> BooleanAttribute("spellCheck"),
        "contextmenu" -> StringAttribute("contextMenu"),
        "autocapitalize" -> StringAttribute("autoCapitalize"),
        "cellpadding" -> StringAttribute("cellPadding"),
        "cellspacing" -> StringAttribute("cellSpacing"),
        "classid"-> StringAttribute("classID"),
      )
  }

  case class ScalaJSReact(newLineAttributes: Boolean) extends ConverterType {
    val attributePrefix: String = "^."
    val nodePrefix: String = "<."
    val customAttributeFunctionName: String = "VdomAttr"

    override val tagNameMap: Map[String, String] =
      Map(
        "title" -> "titleTag",
        "style" -> "styleTag"
      )

    override val attributeNameMap: Map[String, AttributeType] =
      ScalaJSReact.dataAttributes ++ EventAttribute.reactAndLaminarEventAttributes
  }

  case class ScalaTags(newLineAttributes: Boolean) extends ConverterType {
    val attributePrefix: String = ""
    val nodePrefix: String = ""
    val customAttributeFunctionName: String = "attr"

    override val tagNameMap: Map[String, String] =
      Map.empty

    override val attributeNameMap: Map[String, AttributeType] =
      Map(
        "class" -> StringAttribute("cls"),
        "for" -> StringAttribute("`for`"),
        "type" -> StringAttribute("`type`")
      )
  }

  case class Laminar(newLineAttributes: Boolean) extends ConverterType {
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
        "object" -> "objectTag",
        "noscript" -> "noScript",
        "textarea" -> "textArea",
        "optgroup" -> "optGroup",
        "fieldset" -> "fieldSet"
      )

    override val attributeNameMap: Map[String, AttributeType] =
      ScalaJSReact.dataAttributes ++ Map(
        "class" -> StringAttribute("cls"),
        "for" -> StringAttribute("forId"),
        "type" -> StringAttribute("tpe"),
        "value" -> StringAttribute("defaultValue"),
        "checked" -> StringAttribute("defaultChecked"),
        "selected" -> StringAttribute("defaultSelected"),
        "for" -> StringAttribute("forId"),
        "id" -> StringAttribute("idAttr"),
        "max" -> StringAttribute("maxAttr"),
        "min" -> StringAttribute("minAttr"),
        "step" -> StringAttribute("stepAttr"),
        "offset" -> StringAttribute("offsetAttr"),
        "result" -> StringAttribute("resultAttr"),
        "loading" -> StringAttribute("loadingAttr"),
        "style" -> StringAttribute("styleAttr"),
        "content" -> StringAttribute("contentAttr"),
        "form" -> StringAttribute("formId"),
        "height" -> IntAttribute("heightAttr"),
        "width" -> IntAttribute("widthAttr"),
        "list" -> StringAttribute("listId"),
        "contextmenu" -> StringAttribute("contextMenuId"),
      ) ++ EventAttribute.reactAndLaminarEventAttributes
  }
}
