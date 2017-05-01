enablePlugins(ScalaJSPlugin)

// dynamic page reloading
enablePlugins(WorkbenchPlugin)

name := "HtmlToScalaTagsConverter"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.2"

scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "com.lihaoyi" %%% "scalatags" % "0.6.3"
)

