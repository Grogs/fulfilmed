lazy val commonSettings = Seq(
  organization := "me.gregd",
  git.baseVersion := "1.8",
  scalaVersion := "2.12.4",
)

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")

lazy val client = project.enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb, GitVersioning).settings(
  commonSettings,
  name := "fulfilmed-scala-frontend",
  scalaJSUseMainModuleInitializer in Compile := true,
  mainClass in Compile := Some("me.gregd.cineworld.frontend.Main"),
//  scalaJSUseMainModuleInitializer in Test := false,
  scalaJSStage in Test := FastOptStage,
  scalacOptions += "-P:scalajs:sjsDefinedByDefault",
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.full),
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.4",
    "me.shadaj" %%% "slinky-core" % "0.3.2",                 // core React functionality, no React DOM
    "me.shadaj" %%% "slinky-web" % "0.3.2",                  // React DOM, HTML and SVG tags
    "me.shadaj" %%% "slinky-hot" % "0.3.2",                  // Hot loading, requires react-proxy package
  ),
  npmDependencies in Compile ++= Seq(
    "react" -> "16.2.0",
    "react-dom" -> "16.2.0",
    "react-proxy" -> "1.1.8",
    "react-router-dom" -> "4.2.2",
    "history" -> "4.7.2",
  ),
  emitSourceMaps in fullOptJS := true,
//  webpackDevServerExtraArgs := Seq("--inline", "--hot"),
).dependsOn(sharedJs)


lazy val server = project.settings(
  commonSettings,
  name := "fulfilmed",
  dockerRepository := Some("grogs"),
//  dockerUpdateLatest := true,
  buildInfoKeys := Seq[BuildInfoKey](name, version, git.gitHeadCommit, git.gitHeadMessage),
  buildInfoOptions ++= Seq(BuildInfoOption.BuildTime, BuildInfoOption.ToJson),
  buildInfoPackage := "fulfilmed",
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  devCommands in scalaJSPipeline ++= Seq("test", "testOnly"),
  WebKeys.packagePrefix in Assets := "public/",
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % "2.12.4",
    "org.scala-lang" % "scala-compiler" % "2.12.4",
    "info.debatty" % "java-string-similarity" % "1.0.1",
    "org.scalaj" %% "scalaj-http" % "2.3.0",
    "org.json4s" %% "json4s-native" % "3.5.3",
    "org.json4s" %% "json4s-jackson" % "3.5.3",
    "com.google.code.findbugs" % "jsr305" % "3.0.2",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.lihaoyi" %% "scalatags" % "0.6.7",
    "org.typelevel" %% "cats" % "0.9.0",
    "io.monix" %% "monix" % "2.3.2",
    "com.vmunier" %% "scalajs-scripts" % "1.1.1",
    "com.github.cb372" %% "scalacache-memcached" % "0.10.0",
    "com.github.davidmoten" % "rtree" % "0.8.0.4",
    "com.github.pureconfig" %% "pureconfig" % "0.8.0",
    "com.github.pureconfig" %% "pureconfig-enumeratum" % "0.8.0",
    "eu.timepit" %% "refined" % "0.8.6",
    "eu.timepit" %% "refined-pureconfig" % "0.8.6",
    "com.beachape" %% "enumeratum" % "1.5.12",
    "com.softwaremill.macwire" %% "macros" % "2.3.0",
    "com.softwaremill.macwire" %% "util" % "2.3.0",
    ws,
    filters,
    guice,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
    "org.scalatest" %% "scalatest" % "3.0.4" % Test,
    "com.lihaoyi" %% "pprint" % "0.5.3" % Test,
    "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test,
    "com.typesafe.akka" %% "akka-http" % "10.0.11" % Test,
    "com.github.pathikrit" %% "better-files" % "3.2.0",
    "com.typesafe.slick" %% "slick" % "3.2.3",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
    "org.xerial" % "sqlite-jdbc" % "3.23.1",
    "io.circe" %% "circe-core" % "0.9.3",
    "io.circe" %% "circe-generic" % "0.9.3",
    "io.circe" %% "circe-parser" % "0.9.3",
  ),
  libraryDependencies ++= Seq(
    "org.webjars" %% "webjars-play" % "2.6.3",
    "org.webjars" % "font-awesome" % "4.7.0",
  ),
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value)
  .enablePlugins(PlayScala, GitVersioning, BuildInfoPlugin, DeployPlugin, WebScalaJSBundlerPlugin)
  .disablePlugins(PlayLayoutPlugin)
  .dependsOn(sharedJvm)

lazy val shared = crossProject.crossType(CrossType.Pure).settings(
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "upickle" % "0.4.4",
    "com.lihaoyi" %%% "autowire" % "0.2.6",
    "com.lihaoyi" %%% "scalatags" % "0.6.7",
  ),
  commonSettings,
).jsConfigure(_ enablePlugins ScalaJSWeb).enablePlugins(GitVersioning)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

onLoad in Global ~= (_ andThen ("project server" :: _))