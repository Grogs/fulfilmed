import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val commonSettings = Seq(
  organization := "com.fulfilmed",
  git.baseVersion := "1.8",
  scalaVersion := "2.12.15",
  Test / testOptions += Tests.Argument("-oT")
)

run := {
  (server/Compile/run).evaluated
}

Docker/publishLocal := {
  (server/Docker/publishLocal).value
  (ingestor/Docker/publishLocal).value
}

Docker/publish := {
  (server/Docker/publish).value
  (ingestor/Docker/publish).value
}

val testcontainersScalaVersion = "0.39.12"

lazy val client = project.enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb, GitVersioning).settings(
  commonSettings,
  resolvers += Resolver.sonatypeRepo("releases"),
  name := "fulfilmed-scala-frontend",
  Compile / scalaJSUseMainModuleInitializer := true,
  Compile / mainClass := Some("Main"),
  Test / scalaJSStage := FastOptStage,
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  resolvers += "jitpack" at "https://jitpack.io",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "2.0.0",
    "me.shadaj" %%% "slinky-core" % "0.7.0",                 // core React functionality, no React DOM
    "me.shadaj" %%% "slinky-web" % "0.7.0",                  // React DOM, HTML and SVG tags
    "me.shadaj" %%% "slinky-hot" % "0.7.0",                  // Hot loading, requires react-proxy package
    "com.github.cornerman.sloth" %%% "sloth" % "0.4.0",
    "com.lihaoyi" %%% "autowire" % "0.3.3",
    "com.lihaoyi" %%% "scalatags" % "0.11.1",
  ),
  Compile / npmDependencies ++= Seq(
    "react" -> "16.2.0",
    "react-dom" -> "16.2.0",
    "react-proxy" -> "1.1.8",
    "react-router-dom" -> "4.2.2",
    "history" -> "4.7.2",
  ),
  fullOptJS / webpackEmitSourceMaps := true,
  Compile / doc / sources := Seq.empty,
  Compile / packageDoc / publishArtifact := false,
).dependsOn(sharedJs)

lazy val domain = project.settings(
  commonSettings,
  name := "domain",
  resolvers += Resolver.jcenterRepo,
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % "2.12.4",
    "org.scala-lang" % "scala-compiler" % "2.12.4",
    playCore,
    ws,
    "info.debatty" % "java-string-similarity" % "1.0.1",
    "com.google.code.findbugs" % "jsr305" % "3.0.2",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.lihaoyi" %% "scalatags" % "0.11.1",
    "org.typelevel" %% "cats-core" % "2.7.0",
    "org.typelevel" %% "cats-effect" % "2.5.4",
//    "net.andimiller" %% "whales" % "0.13.0",
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
    "com.github.pathikrit" %% "better-files" % "3.2.0",
    "com.typesafe.slick" %% "slick" % "3.2.3",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
    "io.github.nafg" %% "slick-migration-api" % "0.4.2",
    "org.postgresql" % "postgresql" % "42.2.4",
    "org.xerial" % "sqlite-jdbc" % "3.23.1",
    "org.flywaydb" % "flyway-core" % "5.1.4",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
    "org.scalatest" %% "scalatest" % "3.0.4" % Test,
    "com.lihaoyi" %% "pprint" % "0.5.3" % Test,
    "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test,
    "com.typesafe.akka" %% "akka-http" % "10.0.11" % Test,
//    "com.whisk" %% "docker-testkit-scalatest" % "0.9.5" % Test,
//    "com.whisk" %% "docker-testkit-impl-docker-java" % "0.9.5" % Test,
    "com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainersScalaVersion % Test,
    "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersScalaVersion % Test,
    "io.monix" %% "monix" % "3.4.0",
  )
).enablePlugins(GitVersioning).dependsOn(sharedJvm)


lazy val server = project.settings(
  commonSettings,
  name := "fulfilmed",
  dockerRepository := Some("grogs"),
  buildInfoKeys := Seq[BuildInfoKey](name, version, git.gitHeadCommit, git.gitHeadMessage),
  buildInfoOptions ++= Seq(BuildInfoOption.BuildTime, BuildInfoOption.ToJson),
  buildInfoPackage := "fulfilmed",
  scalaJSProjects := Seq(client),
  Assets / pipelineStages := Seq(scalaJSPipeline),
  // TODO devCommands in scalaJSPipeline ++= Seq("test", "testOnly"),
  Assets / WebKeys.packagePrefix := "public/",
  resolvers += Resolver.jcenterRepo,
  resolvers += "jitpack" at "https://jitpack.io",
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % "2.12.4",
    "org.scala-lang" % "scala-compiler" % "2.12.4",
    "com.google.code.findbugs" % "jsr305" % "3.0.2",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.vmunier" %% "scalajs-scripts" % "1.1.1",
    "com.softwaremill.macwire" %% "macros" % "2.3.3",
    "com.softwaremill.macwire" %% "util" % "2.3.3",
    "com.github.cornerman.sloth" %%% "sloth" % "0.4.0",
    "com.lihaoyi" %%% "scalatags" % "0.11.1",
    "com.lihaoyi" %%% "autowire" % "0.3.3",
    ws,
    filters,
  ),
  libraryDependencies ++= Seq(
    "org.webjars" %% "webjars-play" % "2.6.3",
    "org.webjars" % "font-awesome" % "4.7.0",
  ),
  Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value
)
  .enablePlugins(PlayScala, GitVersioning, BuildInfoPlugin, DeployPlugin, WebScalaJSBundlerPlugin)
  .disablePlugins(PlayLayoutPlugin)
  .dependsOn(domain)

lazy val ingestor = project.settings(
  Defaults.itSettings,
  commonSettings,
  name := "fulfilmed-ingestor",
  dockerRepository := Some("grogs"),
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % "2.12.4",
    "org.scala-lang" % "scala-compiler" % "2.12.4",
    "com.google.code.findbugs" % "jsr305" % "3.0.2",
    "io.chrisdavenport" %% "log4cats-core" % "1.1.1",
    "io.chrisdavenport" %% "log4cats-slf4j" % "1.1.1",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.softwaremill.macwire" %% "macros" % "2.3.3",
    "com.softwaremill.macwire" %% "util" % "2.3.3",
//    "net.andimiller" %% "whales" % "0.13.0",
    ws,
    "org.scalatest" %% "scalatest" % "3.0.4" % Test,
  ),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")
).enablePlugins(GitVersioning, DeployPlugin, JavaAppPackaging).dependsOn(domain).configs(IntegrationTest)


lazy val shared = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure).settings(
  libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core" % "0.14.1",
    "io.circe" %%% "circe-generic" % "0.14.1",
    "io.circe" %%% "circe-parser" % "0.14.1",
  ),
  commonSettings,
).jsConfigure(_ enablePlugins ScalaJSWeb).enablePlugins(GitVersioning)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js