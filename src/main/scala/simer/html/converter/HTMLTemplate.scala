package simer.html.converter

import scalatags.JsDom.all._

object HTMLTemplate {

  def template(converter: ConverterType => Unit) =
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
            h4("Scalatags")
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
          td(colspan := "2", textAlign := "center")(
            span("Add attributes on new line: "),
            input(id := "newlineAttributes", cls := "myButton", `type` := "checkbox")
          )
        ),
        tr(width := "100%")(
          td(colspan := "2", textAlign := "center")(
            button(cls := "myButton", onclick := { () => converter(ReactScalaTagsConverter) })("Convert to Scalajs-React's VDOM (1.0.0)"),
            span("  "),
            button(cls := "myButton", onclick := { () => converter(ScalaTagsConverter) })("Convert to Scalatags (0.6.5)")
          )
        )
      )
    )
}
