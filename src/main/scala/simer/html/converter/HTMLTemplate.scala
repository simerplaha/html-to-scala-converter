package simer.html.converter

import org.scalajs.dom
import org.scalajs.dom.html.Input
import scalatags.JsDom.all._
import simer.html.converter.ConverterType.{Outwatch, Laminar, ScalaJSReact, ScalaTags, Tyrian}

object HTMLTemplate {

  def isNewLineAttributes = !dom.document.getElementById("newlineAttributes").asInstanceOf[Input].checked

  def isBooleanTypeConversionDisabled = dom.document.getElementById("disableBooleanTypeConversion").asInstanceOf[Input].checked

  def template(onConvertClicked: ConverterType => Unit) =
    div(
      ul(
        li(
          a(href := "#")("HTML TO SCALA CONVERTER")
        ),
        li(float := "right", paddingTop := 10.px, paddingRight := 10.px,
          u(
            a(
              cls := "github-button",
              href := "https://github.com/simerplaha/html-to-scala-converter",
              attr("data-size") := "large",
              attr("data-show-count") := "true",
              attr("aria-label") := "Star simerplaha/html-to-scala-converter on GitHub",
              "Star"
            )
          )
        )
      ),
      table(width := "100%")(
        tr(width := "100%")(
          th(width := "50%")(
            h4("HTML")
          ),
          th(width := "50%")(
            h4("Scala")
          )
        ),
        tr(width := "100%")(
          td(width := "50%")(
            textarea(id := "htmlCode", cls := "boxsizingBorder", width := "100%", rows := 26, placeholder := "Enter your HTML code here.")(
              """<div class="myClass">
                |    <div class="someClass" data-attribute="someValue">
                |        <button type="button" class="btn btn-default" tabindex="-1">Button</button>
                |    </div>
                |    <br/>
                |    <span>
                |       <img class="my-img-class" src="assets/images/image1.jpg" onclick='alert("clicked!");' alt=""/>
                |    </span>
                |    <a href="javascript:void(0);" class="my-class" data-toggle="dropdown">
                |       Some link
                |    </a>
                |    <ul class="dropdown-menu" style="list-style: none; padding: 0;">
                |       <li>
                |           List item 1
                |       </li>
                |       <li>
                |           List&nbsp;item&nbsp;2
                |       </li>
                |    </ul>
                |    <input type="checkbox" checked />
                |    <input type="checkbox" checked="false" />
                |    <input type="checkbox" checked="blah!" />
                |    <script>
                |       document.getElementById("someId").value = "Hello Scala.js!";
                |    </script>
                |</div>""".stripMargin
            )
          ),
          td(width := "50%")(
            textarea(id := "scalaTagsCode", cls := "boxsizingBorder", width := "100%", rows := 26, placeholder := "Scala code will be generated here.")
          )
        ),
        tr(width := "50%")(
          td(colspan := "1", textAlign := "right", paddingBottom := "10px", paddingRight := "25px")(
            input(`type` := "checkbox", id := "newlineAttributes"),
            label(`for` := "newlineAttributes", "Add attributes on newline"),
          ),
          td(colspan := "1", textAlign := "left", paddingBottom := "10px", paddingLeft := "10px")(
            input(`type` := "checkbox", id := "disableBooleanTypeConversion"),
            label(`for` := "disableBooleanTypeConversion", "Disable Boolean type conversion"),
          )
        ),
        tr(width := "100%")(
          td(colspan := "3", textAlign := "center")(
            button(cls := "button scalajs-react center", onclick := { () => onConvertClicked(ScalaJSReact(isNewLineAttributes, isBooleanTypeConversionDisabled)) })("ScalaJS-React (1.7.7)"),
            span("  "),
            button(cls := "button scalatags center", onclick := { () => onConvertClicked(ScalaTags(isNewLineAttributes, isBooleanTypeConversionDisabled)) })("ScalaTags (0.9.4)"),
            span("  "),
            button(cls := "button laminar center", onclick := { () => onConvertClicked(Laminar(isNewLineAttributes, isBooleanTypeConversionDisabled)) })("Laminar (0.13.1)"),
            span("  "),
            button(cls := "button outwatch center", onclick := { () => onConvertClicked(Outwatch(isNewLineAttributes, isBooleanTypeConversionDisabled)) })("Outwatch (1.0.0-RC7)"),
            span("  "),
            button(cls := "button tyrian center", onclick := { () => onConvertClicked(Tyrian(isNewLineAttributes, isBooleanTypeConversionDisabled)) })("Tyrian (0.5.1)")

          ),
        ),
        tr(width := "100%")(
          td(colspan := "3", textAlign := "center")(
            p(
              strong(""""Add attributes on newline""""),
              " inserts a new line after each tag attribute/property.",
            ),
            p(
              strong(""""Disable Boolean type conversion""""),
              " converts ",
              span(backgroundColor := "#dddcdc" ,"""checked = "blah!""""),
              " to ",
              span(backgroundColor := "#dddcdc" ,"""checked := "blah!""""),
              " instead of ",
              span(backgroundColor := "#dddcdc" ,"""checked := true"""),
            ),
            p(
              "Type conversions are enabled for ScalaJS-React, Laminar & Outwatch. Please report any missing types."
            )
          )
        )
      )
    )
}
