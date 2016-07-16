import sbt.Keys._
import sbt.Project.projectToRef

val versions = new {
  val json4s = "3.2.11"
  val circe = "0.4.1"
  val scala = "2.11.7"
  val scalacss = "0.4.1"
  val scalajsReact = "0.11.1"
  val scalaJsTime = "0.1.0"
  val shapeless = "2.3.1"

}

lazy val commonSettings = Seq(
  organization := "me.gregd",
  version := "1.1",
  scalaVersion := "2.11.7"
)

scalacOptions += "-target:jvm-1.7" //my vps is stuck on java 7 for the moment
javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq("-Xfatal-warnings", "-feature")

lazy val client: Project = project
  .settings(
    skip in packageJSDependencies := false,
    libraryDependencies ++= Seq(
      "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
      "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,
      "com.github.japgolly.scalacss" %%% "core" % versions.scalacss,
      "com.github.japgolly.scalacss" %%% "ext-react" % versions.scalacss,
      "org.scala-js" %%% "scalajs-java-time" % versions.scalaJsTime
    ),
    jsDependencies ++= Seq(
      "org.webjars.bower" % "react" % "15.0.1"
        /        "react-with-addons.js"
        minified "react-with-addons.min.js"
        commonJSName "React",
      "org.webjars.bower" % "react" % "15.0.1"
        /         "react-dom.js"
        minified  "react-dom.min.js"
        dependsOn "react-with-addons.js"
        commonJSName "ReactDOM"
    )
)
  .enablePlugins(ScalaJSPlugin, ScalaJSPlay)
  .dependsOn(sharedJs)

lazy val jsProjects = Seq(client)

lazy val server: Project = project
  .settings(commonSettings: _*)
  .settings(
    //compile in Compile <<= (compile in Compile) dependsOn (fastOptJS in (`scala-frontend`, Compile)),
    scalaJSProjects := jsProjects,
    pipelineStages := Seq(scalaJSProd),
    name := "fulfilmed-backend",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-library" % versions.scala,
      "org.scala-lang" % "scala-compiler" % versions.scala,
      "com.rockymadden.stringmetric" % "stringmetric-core_2.11" % "0.27.4",
      "com.chuusai" % "shapeless_2.11" % versions.shapeless,
      "com.typesafe.slick" % "slick_2.11" % "2.1.0",
      "com.h2database" % "h2" % "1.3.164",
      "org.scalaj" % "scalaj-http_2.11" % "1.1.5",
      "org.json4s" % "json4s-native_2.11" % versions.json4s,
      "org.json4s" % "json4s-jackson_2.11" % versions.json4s,
      "org.scalatra" % "scalatra_2.11" % "2.3.1",
      "org.scalatra" % "scalatra-json_2.11" % "2.3.1",
      "com.google.guava" % "guava" % "16.0.1",
      "com.google.code.findbugs" % "jsr305" % "3.0.1",
      "com.typesafe.scala-logging" % "scala-logging-slf4j_2.11" % "2.1.2",
      "ch.qos.logback" % "logback-classic" % "1.0.13",
      "org.jsoup" % "jsoup" % "1.7.3",
      "org.feijoas" % "mango_2.11" % "0.11", // exclude("jsr305"),
      "com.lihaoyi" %% "scalatags" % "0.5.4",
      ws,
      "org.scalatra" % "scalatra-scalatest_2.11" % "2.3.1" % "test",
      "org.scalatest" % "scalatest_2.11" % "1.9.1" % "test",
      "com.lihaoyi" %% "scalatags" % "0.5.4",
      "io.circe" %% "circe-core" % versions.circe,
      "io.circe" %% "circe-generic" % versions.circe,
      "io.circe" %% "circe-parser" % versions.circe,
      "com.typesafe.play" %% "play-ws" % "2.4.3"
    )
    ,
    //Webjars
    libraryDependencies ++= Seq(
      "org.webjars" %% "webjars-play" % "2.4.0-1",
      "org.webjars" % "font-awesome" % "4.5.0"
    )
  )
  .enablePlugins(PlayScala, PlayScalaJS)
  .disablePlugins(PlayLayoutPlugin)
  .aggregate(jsProjects.map(projectToRef): _*)
  .dependsOn(sharedJvm)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "0.3.6",
      "com.lihaoyi" %%% "autowire" % "0.2.4",
      "com.lihaoyi" %%% "scalatags" % "0.5.2"
    )
  )
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

routesGenerator := InjectedRoutesGenerator

test in assembly := {}

//mainClass in (Compile,run) := Some("me.gregd.cineworld.util.JettyBootstrap")

resolvers += "Sonatype OSS Releases" at  "http://oss.sonatype.org/content/groups/public"

resolvers += "mandubian-snapshots" at  "http://github.com/mandubian/mandubian-mvn/raw/master/snapshots/"

resolvers += "mandubian-releases" at  "http://github.com/mandubian/mandubian-mvn/raw/master/releases/"

resolvers += "typesafe-releases" at  "http://repo.typesafe.com/typesafe/releases/"

resolvers += "sonatype-snapshots" at  "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "sonatype-releases" at  "http://oss.sonatype.org/content/repositories/releases"

resolvers += "eclipse" at  "http://download.eclipse.org/rt/eclipselink/maven.repo"

