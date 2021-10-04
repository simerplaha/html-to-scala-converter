package simer.html.converter

import org.scalajs.dom
import org.scalajs.dom.html.Input

import scalatags.JsDom.all._

object HTMLTemplate {

  def isNewLineAttributes = !dom.document.getElementById("newlineAttributes").asInstanceOf[Input].checked

  def template(onConvertClicked: ConverterType => Unit) =
    div(
      ul(
        li(
          a(href := "#")("HTML TO SCALATAGS CONVERTER")
        ),
        li(float := "right",
          u(
            a(href := "https://github.com/simerplaha/html-to-scalatags-converter/issues", target := "blank")("Report an issue")
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
                |        <button type="button" class="btn btn-default">Button</button>
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
        tr(width := "100%")(
          td(colspan := "2", textAlign := "center", paddingBottom := "10px")(
            input(`type` := "checkbox", id := "newlineAttributes"),
            label(`for` := "newlineAttributes", "Add properties on newline"),
          )
        ),
        tr(width := "100%")(
          td(colspan := "2", textAlign := "center")(
            button(cls := "button -salmon center", onclick := { () => onConvertClicked(ReactScalaTagsConverter(isNewLineAttributes)) })("Convert to Scalajs-React's VDOM (1.0.0)"),
            span("  "),
            button(cls := "button -salmon center", onclick := { () => onConvertClicked(ScalaTagsConverter(isNewLineAttributes)) })("Convert to Scalatags (0.6.5)")
          )
        ),
        tr(width := "100%")(
          td(colspan := "2", textAlign := "center", paddingTop := "5px")(
            a(cls := "github-button", href := "https://github.com/simerplaha/html-to-scalatags-converter", attr("data-icon") := "octicon-star", attr("data-size") := "large", attr("data-show-count") := "true", attr("aria-label") := "Star simerplaha/html-to-scalatags-converter on GitHub", "Star"),
          )
        )
      )
    )
}
