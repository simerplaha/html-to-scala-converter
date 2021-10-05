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

object ConverterType {
  //event names in Scala are camel cased for Laminar and Scalajs-react.
  val eventAttributeNameMap =
    Map(
      s"onchange" -> ScalaAttributeNameMap("onChange", "-->"),
      s"onclick" -> ScalaAttributeNameMap("onClick", "-->"),
      s"onclickcapture" -> ScalaAttributeNameMap("onClickCapture", "-->"),
      s"onetimecode" -> ScalaAttributeNameMap("oneTimeCode", "-->"),
      s"onabort" -> ScalaAttributeNameMap("onAbort", "-->"),
      s"onabortcapture" -> ScalaAttributeNameMap("onAbortCapture", "-->"),
      s"onanimationend" -> ScalaAttributeNameMap("onAnimationEnd", "-->"),
      s"onanimationendcapture" -> ScalaAttributeNameMap("onAnimationEndCapture", "-->"),
      s"onanimationiteration" -> ScalaAttributeNameMap("onAnimationIteration", "-->"),
      s"onanimationiterationcapture" -> ScalaAttributeNameMap("onAnimationIterationCapture", "-->"),
      s"onanimationstart" -> ScalaAttributeNameMap("onAnimationStart", "-->"),
      s"onanimationstartcapture" -> ScalaAttributeNameMap("onAnimationStartCapture", "-->"),
      s"onauxclick" -> ScalaAttributeNameMap("onAuxClick", "-->"),
      s"onauxclickcapture" -> ScalaAttributeNameMap("onAuxClickCapture", "-->"),
      s"onbeforeinput" -> ScalaAttributeNameMap("onBeforeInput", "-->"),
      s"onblur" -> ScalaAttributeNameMap("onBlur", "-->"),
      s"onblurcapture" -> ScalaAttributeNameMap("onBlurCapture", "-->"),
      s"oncanplay" -> ScalaAttributeNameMap("onCanPlay", "-->"),
      s"oncanplaycapture" -> ScalaAttributeNameMap("onCanPlayCapture", "-->"),
      s"oncanplaythrough" -> ScalaAttributeNameMap("onCanPlayThrough", "-->"),
      s"oncompositionend" -> ScalaAttributeNameMap("onCompositionEnd", "-->"),
      s"oncompositionstart" -> ScalaAttributeNameMap("onCompositionStart", "-->"),
      s"oncompositionupdate" -> ScalaAttributeNameMap("onCompositionUpdate", "-->"),
      s"oncontextmenu" -> ScalaAttributeNameMap("onContextMenu", "-->"),
      s"oncontextmenucapture" -> ScalaAttributeNameMap("onContextMenuCapture", "-->"),
      s"oncopy" -> ScalaAttributeNameMap("onCopy", "-->"),
      s"oncopycapture" -> ScalaAttributeNameMap("onCopyCapture", "-->"),
      s"oncut" -> ScalaAttributeNameMap("onCut", "-->"),
      s"oncutcapture" -> ScalaAttributeNameMap("onCutCapture", "-->"),
      s"ondblclick" -> ScalaAttributeNameMap("onDblClick", "-->"),
      s"ondoubleclick" -> ScalaAttributeNameMap("onDoubleClick", "-->"),
      s"ondoubleclickcapture" -> ScalaAttributeNameMap("onDoubleClickCapture", "-->"),
      s"ondrag" -> ScalaAttributeNameMap("onDrag", "-->"),
      s"ondragcapture" -> ScalaAttributeNameMap("onDragCapture", "-->"),
      s"ondragend" -> ScalaAttributeNameMap("onDragEnd", "-->"),
      s"ondragendcapture" -> ScalaAttributeNameMap("onDragEndCapture", "-->"),
      s"ondragenter" -> ScalaAttributeNameMap("onDragEnter", "-->"),
      s"ondragentercapture" -> ScalaAttributeNameMap("onDragEnterCapture", "-->"),
      s"ondragexit" -> ScalaAttributeNameMap("onDragExit", "-->"),
      s"ondragexitcapture" -> ScalaAttributeNameMap("onDragExitCapture", "-->"),
      s"ondragleave" -> ScalaAttributeNameMap("onDragLeave", "-->"),
      s"ondragleavecapture" -> ScalaAttributeNameMap("onDragLeaveCapture", "-->"),
      s"ondragover" -> ScalaAttributeNameMap("onDragOver", "-->"),
      s"ondragovercapture" -> ScalaAttributeNameMap("onDragOverCapture", "-->"),
      s"ondragstart" -> ScalaAttributeNameMap("onDragStart", "-->"),
      s"ondragstartcapture" -> ScalaAttributeNameMap("onDragStartCapture", "-->"),
      s"ondrop" -> ScalaAttributeNameMap("onDrop", "-->"),
      s"ondropcapture" -> ScalaAttributeNameMap("onDropCapture", "-->"),
      s"ondurationchange" -> ScalaAttributeNameMap("onDurationChange", "-->"),
      s"ondurationchangecapture" -> ScalaAttributeNameMap("onDurationChangeCapture", "-->"),
      s"onemptied" -> ScalaAttributeNameMap("onEmptied", "-->"),
      s"onemptiedcapture" -> ScalaAttributeNameMap("onEmptiedCapture", "-->"),
      s"onencrypted" -> ScalaAttributeNameMap("onEncrypted", "-->"),
      s"onencryptedcapture" -> ScalaAttributeNameMap("onEncryptedCapture", "-->"),
      s"onended" -> ScalaAttributeNameMap("onEnded", "-->"),
      s"onendedcapture" -> ScalaAttributeNameMap("onEndedCapture", "-->"),
      s"onerror" -> ScalaAttributeNameMap("onError", "-->"),
      s"onerrorcapture" -> ScalaAttributeNameMap("onErrorCapture", "-->"),
      s"onfocus" -> ScalaAttributeNameMap("onFocus", "-->"),
      s"onfocuscapture" -> ScalaAttributeNameMap("onFocusCapture", "-->"),
      s"oninput" -> ScalaAttributeNameMap("onInput", "-->"),
      s"oninputcapture" -> ScalaAttributeNameMap("onInputCapture", "-->"),
      s"oninvalid" -> ScalaAttributeNameMap("onInvalid", "-->"),
      s"oninvalidcapture" -> ScalaAttributeNameMap("onInvalidCapture", "-->"),
      s"onkeydown" -> ScalaAttributeNameMap("onKeyDown", "-->"),
      s"onkeydowncapture" -> ScalaAttributeNameMap("onKeyDownCapture", "-->"),
      s"onkeypress" -> ScalaAttributeNameMap("onKeyPress", "-->"),
      s"onkeypresscapture" -> ScalaAttributeNameMap("onKeyPressCapture", "-->"),
      s"onkeyup" -> ScalaAttributeNameMap("onKeyUp", "-->"),
      s"onkeyupcapture" -> ScalaAttributeNameMap("onKeyUpCapture", "-->"),
      s"onload" -> ScalaAttributeNameMap("onLoad", "-->"),
      s"onloadcapture" -> ScalaAttributeNameMap("onLoadCapture", "-->"),
      s"onloadstart" -> ScalaAttributeNameMap("onLoadStart", "-->"),
      s"onloadstartcapture" -> ScalaAttributeNameMap("onLoadStartCapture", "-->"),
      s"onloadeddata" -> ScalaAttributeNameMap("onLoadedData", "-->"),
      s"onloadeddatacapture" -> ScalaAttributeNameMap("onLoadedDataCapture", "-->"),
      s"onloadedmetadata" -> ScalaAttributeNameMap("onLoadedMetadata", "-->"),
      s"onloadedmetadatacapture" -> ScalaAttributeNameMap("onLoadedMetadataCapture", "-->"),
      s"onmousedown" -> ScalaAttributeNameMap("onMouseDown", "-->"),
      s"onmousedowncapture" -> ScalaAttributeNameMap("onMouseDownCapture", "-->"),
      s"onmouseenter" -> ScalaAttributeNameMap("onMouseEnter", "-->"),
      s"onmouseleave" -> ScalaAttributeNameMap("onMouseLeave", "-->"),
      s"onmousemove" -> ScalaAttributeNameMap("onMouseMove", "-->"),
      s"onmousemovecapture" -> ScalaAttributeNameMap("onMouseMoveCapture", "-->"),
      s"onmouseout" -> ScalaAttributeNameMap("onMouseOut", "-->"),
      s"onmouseoutcapture" -> ScalaAttributeNameMap("onMouseOutCapture", "-->"),
      s"onmouseover" -> ScalaAttributeNameMap("onMouseOver", "-->"),
      s"onmouseovercapture" -> ScalaAttributeNameMap("onMouseOverCapture", "-->"),
      s"onmouseup" -> ScalaAttributeNameMap("onMouseUp", "-->"),
      s"onmouseupcapture" -> ScalaAttributeNameMap("onMouseUpCapture", "-->"),
      s"onpaste" -> ScalaAttributeNameMap("onPaste", "-->"),
      s"onpastecapture" -> ScalaAttributeNameMap("onPasteCapture", "-->"),
      s"onpause" -> ScalaAttributeNameMap("onPause", "-->"),
      s"onpausecapture" -> ScalaAttributeNameMap("onPauseCapture", "-->"),
      s"onplay" -> ScalaAttributeNameMap("onPlay", "-->"),
      s"onplaycapture" -> ScalaAttributeNameMap("onPlayCapture", "-->"),
      s"onplaying" -> ScalaAttributeNameMap("onPlaying", "-->"),
      s"onplayingcapture" -> ScalaAttributeNameMap("onPlayingCapture", "-->"),
      s"ongotpointercapture" -> ScalaAttributeNameMap("onGotPointerCapture", "-->"),
      s"onlostpointercapture" -> ScalaAttributeNameMap("onLostPointerCapture", "-->"),
      s"onpointercancel" -> ScalaAttributeNameMap("onPointerCancel", "-->"),
      s"onpointerdown" -> ScalaAttributeNameMap("onPointerDown", "-->"),
      s"onpointerenter" -> ScalaAttributeNameMap("onPointerEnter", "-->"),
      s"onpointerleave" -> ScalaAttributeNameMap("onPointerLeave", "-->"),
      s"onpointermove" -> ScalaAttributeNameMap("onPointerMove", "-->"),
      s"onpointerout" -> ScalaAttributeNameMap("onPointerOut", "-->"),
      s"onpointerover" -> ScalaAttributeNameMap("onPointerOver", "-->"),
      s"onpointerup" -> ScalaAttributeNameMap("onPointerUp", "-->"),
      s"onprogress" -> ScalaAttributeNameMap("onProgress", "-->"),
      s"onprogresscapture" -> ScalaAttributeNameMap("onProgressCapture", "-->"),
      s"onratechange" -> ScalaAttributeNameMap("onRateChange", "-->"),
      s"onratechangecapture" -> ScalaAttributeNameMap("onRateChangeCapture", "-->"),
      s"onreset" -> ScalaAttributeNameMap("onReset", "-->"),
      s"onresetcapture" -> ScalaAttributeNameMap("onResetCapture", "-->"),
      s"onscroll" -> ScalaAttributeNameMap("onScroll", "-->"),
      s"onscrollcapture" -> ScalaAttributeNameMap("onScrollCapture", "-->"),
      s"onseeked" -> ScalaAttributeNameMap("onSeeked", "-->"),
      s"onseekedcapture" -> ScalaAttributeNameMap("onSeekedCapture", "-->"),
      s"onseeking" -> ScalaAttributeNameMap("onSeeking", "-->"),
      s"onseekingcapture" -> ScalaAttributeNameMap("onSeekingCapture", "-->"),
      s"onselect" -> ScalaAttributeNameMap("onSelect", "-->"),
      s"onstalled" -> ScalaAttributeNameMap("onStalled", "-->"),
      s"onstalledcapture" -> ScalaAttributeNameMap("onStalledCapture", "-->"),
      s"onsubmit" -> ScalaAttributeNameMap("onSubmit", "-->"),
      s"onsubmitcapture" -> ScalaAttributeNameMap("onSubmitCapture", "-->"),
      s"onsuspend" -> ScalaAttributeNameMap("onSuspend", "-->"),
      s"onsuspendcapture" -> ScalaAttributeNameMap("onSuspendCapture", "-->"),
      s"ontimeupdate" -> ScalaAttributeNameMap("onTimeUpdate", "-->"),
      s"ontimeupdatecapture" -> ScalaAttributeNameMap("onTimeUpdateCapture", "-->"),
      s"ontouchcancel" -> ScalaAttributeNameMap("onTouchCancel", "-->"),
      s"ontouchcancelcapture" -> ScalaAttributeNameMap("onTouchCancelCapture", "-->"),
      s"ontouchend" -> ScalaAttributeNameMap("onTouchEnd", "-->"),
      s"ontouchendcapture" -> ScalaAttributeNameMap("onTouchEndCapture", "-->"),
      s"ontouchmove" -> ScalaAttributeNameMap("onTouchMove", "-->"),
      s"ontouchmovecapture" -> ScalaAttributeNameMap("onTouchMoveCapture", "-->"),
      s"ontouchstart" -> ScalaAttributeNameMap("onTouchStart", "-->"),
      s"ontouchstartcapture" -> ScalaAttributeNameMap("onTouchStartCapture", "-->"),
      s"ontransitionend" -> ScalaAttributeNameMap("onTransitionEnd", "-->"),
      s"ontransitionendcapture" -> ScalaAttributeNameMap("onTransitionEndCapture", "-->"),
      s"onvolumechange" -> ScalaAttributeNameMap("onVolumeChange", "-->"),
      s"onvolumechangecapture" -> ScalaAttributeNameMap("onVolumeChangeCapture", "-->"),
      s"onwaiting" -> ScalaAttributeNameMap("onWaiting", "-->"),
      s"onwaitingcapture" -> ScalaAttributeNameMap("onWaitingCapture", "-->"),
      s"onwheel" -> ScalaAttributeNameMap("onWheel", "-->"),
      s"onwheelcapture" -> ScalaAttributeNameMap("onWheelCapture", "-->")
    )
}

case class ReactScalaTagsConverter(newLineAttributes: Boolean) extends ConverterType {
  val attributePrefix: String = "^."
  val nodePrefix: String = "<."
  val customAttributeFunctionName: String = "VdomAttr"

  override val tagNameMap: Map[String, String] =
    Map(
      "title" -> "titleTag",
    )

  override val attributeNameMap: Map[String, ScalaAttributeNameMap] =
    Map(
      "class" -> ScalaAttributeNameMap("className"),
      "for" -> ScalaAttributeNameMap("`for`"),
      "type" -> ScalaAttributeNameMap("`type`"),
      "content" -> ScalaAttributeNameMap("contentAttr"),
      "hreflang" -> ScalaAttributeNameMap("hrefLang"),
      "tabindex" -> ScalaAttributeNameMap("tabIndex"),
      "styleTag" -> ScalaAttributeNameMap("styleTag"),
      "autofocus" -> ScalaAttributeNameMap("autoFocus"),
      "autocomplete" -> ScalaAttributeNameMap("autoComplete"),
    ) ++ ConverterType.eventAttributeNameMap
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
      "object" -> "objectTag",
      "noscript" -> "noScript",
      "textarea" -> "textArea"
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
      "hreflang" -> ScalaAttributeNameMap("hrefLang"),
      "tabindex" -> ScalaAttributeNameMap("tabIndex"),
      "autofocus" -> ScalaAttributeNameMap("autoFocus"),
      "autocomplete" -> ScalaAttributeNameMap("autoComplete"),
      "readonly" -> ScalaAttributeNameMap("readOnly"),
    ) ++ ConverterType.eventAttributeNameMap
}
