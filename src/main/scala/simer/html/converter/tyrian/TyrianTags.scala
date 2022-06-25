package simer.html.converter.tyrian

object TyrianTags {

  def tags: List[TagType] =
    List(
      HasChildren("a"),
      HasChildren("abbr"),
      HasChildren("address"),
      NoChildren("area"),
      HasChildren("article"),
      HasChildren("aside"),
      HasChildren("audio"),
      HasChildren("b"),
      NoChildren("base"),
      HasChildren("bdi"),
      HasChildren("bdo"),
      HasChildren("blockquote"),
      HasChildren("body"),
      NoChildren("br"),
      OptionalChildren("button"),
      HasChildren("canvas"),
      HasChildren("caption"),
      HasChildren("cite"),
      HasChildren("code"),
      NoChildren("col"),
      HasChildren("colgroup"),
      HasChildren("data"),
      HasChildren("datalist"),
      HasChildren("dd"),
      HasChildren("del"),
      HasChildren("details"),
      HasChildren("dfn"),
      HasChildren("dialog"),
      HasChildren("div"),
      HasChildren("dl"),
      HasChildren("dt"),
      HasChildren("em"),
      HasChildren("embed"),
      HasChildren("fieldset"),
      HasChildren("figcaption"),
      HasChildren("figure"),
      HasChildren("footer"),
      HasChildren("form"),
      HasChildren("h1"),
      HasChildren("h2"),
      HasChildren("h3"),
      HasChildren("h4"),
      HasChildren("h5"),
      HasChildren("h6"),
      HasChildren("head"),
      HasChildren("header"),
      NoChildren("hr"),
      HasChildren("html"),
      HasChildren("i"),
      HasChildren("iframe"),
      NoChildren("img"),
      NoChildren("input"),
      HasChildren("ins"),
      HasChildren("kbd"),
      HasChildren("label"),
      HasChildren("legend"),
      HasChildren("li"),
      NoChildren("link"),
      HasChildren("main"),
      HasChildren("map"),
      HasChildren("mark"),
      NoChildren("meta"),
      HasChildren("meter"),
      HasChildren("nav"),
      HasChildren("noscript"),
      HasChildren("`object`", "object"),
      HasChildren("objectTag", "object"),
      HasChildren("ol"),
      HasChildren("optgroup"),
      HasChildren("option"),
      HasChildren("output"),
      HasChildren("p"),
      NoChildren("param"),
      HasChildren("picture"),
      HasChildren("pre"),
      HasChildren("progress"),
      HasChildren("q"),
      HasChildren("rp"),
      HasChildren("rt"),
      HasChildren("ruby"),
      HasChildren("s"),
      HasChildren("samp"),
      HasChildren("script"),
      HasChildren("section"),
      HasChildren("select"),
      HasChildren("small"),
      NoChildren("source"),
      HasChildren("span"),
      HasChildren("strong"),
      HasChildren("style"),
      HasChildren("sub"),
      HasChildren("summary"),
      HasChildren("sup"),
      HasChildren("svg"),
      HasChildren("table"),
      HasChildren("tbody"),
      HasChildren("td"),
      HasChildren("template"),
      HasChildren("textarea"),
      HasChildren("tfoot"),
      HasChildren("th"),
      HasChildren("thead"),
      HasChildren("time"),
      HasChildren("title"),
      HasChildren("tr"),
      NoChildren("track"),
      HasChildren("u"),
      HasChildren("ul"),
      HasChildren("`var`", "var"),
      HasChildren("varTag", "var"),
      HasChildren("video"),
      HasChildren("wbr"),
      NoChildren("animate"),
      NoChildren("animateColor"),
      NoChildren("animateMotion"),
      NoChildren("animateTransform"),
      NoChildren("circle"),
      HasChildren("clipPath"),
      HasChildren("defs"),
      HasChildren("desc"),
      NoChildren("ellipse"),
      NoChildren("feBlend"),
      NoChildren("feColorMatrix"),
      HasChildren("feComponentTransfer"),
      NoChildren("feComposite"),
      NoChildren("feConvolveMatrix"),
      HasChildren("feDiffuseLighting"),
      NoChildren("feDisplacementMap"),
      NoChildren("feDistantLight"),
      NoChildren("feFlood"),
      NoChildren("feFuncA"),
      NoChildren("feFuncB"),
      NoChildren("feFuncG"),
      NoChildren("feFuncR"),
      NoChildren("feGaussianBlur"),
      NoChildren("feImage"),
      HasChildren("feMerge"),
      NoChildren("feMergeNode"),
      NoChildren("feMorphology"),
      NoChildren("feOffset"),
      NoChildren("fePointLight"),
      HasChildren("feSpecularLighting"),
      NoChildren("feSpotLight"),
      HasChildren("feTile"),
      NoChildren("feTurbulence"),
      HasChildren("filter"),
      HasChildren("foreignObject"),
      HasChildren("g"),
      NoChildren("image"),
      NoChildren("line"),
      HasChildren("linearGradient"),
      HasChildren("marker"),
      HasChildren("mask"),
      HasChildren("metadata"),
      NoChildren("mpath"),
      NoChildren("path"),
      HasChildren("pattern"),
      NoChildren("polygon"),
      NoChildren("polyline"),
      HasChildren("radialGradient"),
      NoChildren("rect"),
      NoChildren("set"),
      NoChildren("stop"),
      HasChildren("switch"),
      HasChildren("symbol"),
      HasChildren("textTag", "text"),
      HasChildren("textPath"),
      HasChildren("tspan"),
      NoChildren("use"),
      NoChildren("view")
    )

  sealed trait TagType {
    def name: String

    def tag: Option[String]
  }

  final case class HasChildren(name: String, tag: Option[String]) extends TagType

  object HasChildren {
    def apply(name: String): HasChildren = HasChildren(name, None)

    def apply(name: String, tag: String): HasChildren = HasChildren(name, Some(tag))
  }

  final case class NoChildren(name: String, tag: Option[String]) extends TagType

  object NoChildren {
    def apply(name: String): NoChildren = NoChildren(name, None)

    def apply(name: String, tag: String): NoChildren = NoChildren(name, Some(tag))
  }

  final case class OptionalChildren(name: String, tag: Option[String]) extends TagType

  object OptionalChildren {
    def apply(name: String): OptionalChildren = OptionalChildren(name, None)

    def apply(name: String, tag: String): OptionalChildren = OptionalChildren(name, Some(tag))
  }
}



