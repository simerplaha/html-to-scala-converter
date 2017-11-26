enablePlugins(ScalaJSPlugin)
enablePlugins(WorkbenchPlugin)

name := "HtmlToScalaTagsConverter"

version := "1.0.0"

scalaVersion := "2.12.2"

scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "com.lihaoyi" %%% "scalatags" % "0.6.7"
)