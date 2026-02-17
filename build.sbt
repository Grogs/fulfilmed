import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val commonSettings = Seq(
  organization := "com.fulfilmed",
  git.baseVersion := "1.8",
  scalaVersion := "3.8.1",
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

val testcontainersScalaVersion = "0.41.4"
val http4sVersion = "0.23.31"

lazy val client = project.enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb, GitVersioning).settings(
  commonSettings,
  resolvers += Resolver.sonatypeRepo("releases"),
  name := "fulfilmed-scala-frontend",
  Compile / scalaJSUseMainModuleInitializer := true,
  Compile / mainClass := Some("Main"),
  Test / scalaJSStage := FastOptStage,
  resolvers += "jitpack" at "https://jitpack.io",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "2.8.0",
    "com.github.japgolly.scalajs-react" %%% "core" % "2.1.2",
    "com.github.japgolly.scalajs-react" %%% "extra" % "2.1.2",
    "io.github.cquiroz" %%% "scala-java-time" % "2.6.0",
    "io.circe" %%% "circe-core" % "0.14.10",
    "io.circe" %%% "circe-generic" % "0.14.10",
    "io.circe" %%% "circe-parser" % "0.14.10",
  ),
  Compile / npmDependencies ++= Seq(
    "react" -> "18.2.0",
    "react-dom" -> "18.2.0",
  ),
  fullOptJS / webpackEmitSourceMaps := true,
  Compile / doc / sources := Seq.empty,
  Compile / packageDoc / publishArtifact := false,
).dependsOn(sharedJs)

lazy val domain = project.settings(
  commonSettings,
  name := "domain",
  resolvers += Resolver.jcenterRepo,
  libraryDependencies ++= Seq(
    "org.playframework" %% "play" % "3.0.6",
    "org.playframework" %% "play-ws" % "3.0.6",
    "info.debatty" % "java-string-similarity" % "2.0.0",
    "com.google.code.findbugs" % "jsr305" % "3.0.2",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
    "ch.qos.logback" % "logback-classic" % "1.5.12",
    "com.lihaoyi" %% "scalatags" % "0.13.1",
    "org.typelevel" %% "cats-core" % "2.13.0",
    "org.typelevel" %% "log4cats-slf4j" % "2.7.0",
    "com.vmunier" %% "scalajs-scripts" % "1.3.0",
    "com.github.cb372" %% "scalacache-caffeine" % "1.0.0-M6",
    "com.github.davidmoten" % "rtree2" % "0.9.3",
    "com.github.pureconfig" %% "pureconfig-core" % "0.17.8",
    "com.github.pureconfig" %% "pureconfig-generic-scala3" % "0.17.8",
    "com.github.pureconfig" %% "pureconfig-enumeratum" % "0.17.8",
    "eu.timepit" %% "refined" % "0.11.2",
    "eu.timepit" %% "refined-pureconfig" % "0.11.2",
    "com.beachape" %% "enumeratum" % "1.7.5",
    "com.softwaremill.macwire" %% "macros" % "2.6.4",
    "com.softwaremill.macwire" %% "util" % "2.6.4",
    "com.github.pathikrit" %% "better-files" % "3.9.2",
    "com.typesafe.slick" %% "slick" % "3.5.2",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.5.2",
    "io.github.nafg.slick-migration-api" %% "slick-migration-api" % "0.10.0",
    "org.postgresql" % "postgresql" % "42.7.4",
    "org.xerial" % "sqlite-jdbc" % "3.47.1.0",
    "org.flywaydb" % "flyway-core" % "10.21.0",
    "com.github.cb372" %% "cats-retry" % "3.1.3",
    "org.systemfw" %% "upperbound" % "0.4.0",
    "co.fs2" %% "fs2-io" % "3.12.1",
    "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
    "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    "com.lihaoyi" %% "pprint" % "0.9.0" % Test,
    "org.scalamock" %% "scalamock" % "6.0.0" % Test,
    "org.apache.pekko" %% "pekko-http" % "1.1.0" % Test,
    "com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainersScalaVersion % Test,
    "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersScalaVersion % Test,
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
  libraryDependencies ++= Seq(
    "com.google.code.findbugs" % "jsr305" % "3.0.2",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
    "ch.qos.logback" % "logback-classic" % "1.5.12",
    "com.vmunier" %% "scalajs-scripts" % "1.3.0",
    "com.softwaremill.macwire" %% "macros" % "2.6.4",
    "com.softwaremill.macwire" %% "util" % "2.6.4",
    "com.lihaoyi" %%% "scalatags" % "0.13.1",
    "org.http4s" %% "http4s-ember-client" % http4sVersion,
    "org.http4s" %% "http4s-ember-server" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "io.circe" %% "circe-core" % "0.14.10",
    "io.circe" %% "circe-generic" % "0.14.10",
    "io.circe" %% "circe-parser" % "0.14.10",
  ),
  libraryDependencies ++= Seq(
    "org.webjars" %% "webjars-play" % "3.0.2",
    "org.webjars.npm" % "font-awesome" % "4.7.0",
  ),
  Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value,
  routesGenerator := InjectedRoutesGenerator
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
    "com.google.code.findbugs" % "jsr305" % "3.0.2",
    "com.softwaremill.macwire" %% "macros" % "2.6.4",
    "com.softwaremill.macwire" %% "util" % "2.6.4",
    "org.playframework" %% "play-ws" % "3.0.6",
    "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  ),
).enablePlugins(GitVersioning, DeployPlugin, JavaAppPackaging).dependsOn(domain).configs(IntegrationTest)


lazy val shared = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure).settings(
  libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core" % "0.14.10",
    "io.circe" %%% "circe-generic" % "0.14.10",
    "io.circe" %%% "circe-parser" % "0.14.10",
    "org.typelevel" %%% "cats-effect" % "3.6.0",
  ),
  commonSettings,
).jsConfigure(_ enablePlugins ScalaJSWeb).enablePlugins(GitVersioning)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js