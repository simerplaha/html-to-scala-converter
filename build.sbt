enablePlugins(ScalaJSPlugin)

name := "html-to-scala-converter"

version := "1.0.0"

scalaVersion := "2.13.7"

scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "1.2.0",
  "com.lihaoyi" %%% "scalatags" % "0.9.4"
)
