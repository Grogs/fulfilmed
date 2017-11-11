lazy val commonSettings = Seq(
  organization := "me.gregd",
  version := "1.6",
  scalaVersion := "2.12.4"
)

lazy val deploy = taskKey[Unit]("Deploy docker image with dokku")

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")

lazy val client = project.enablePlugins(ScalaJSPlugin, ScalaJSWeb).settings(
  commonSettings,
  name := "fulfilmed-scala-frontend",
  scalaJSUseMainModuleInitializer in Compile := true,
  mainClass in Compile := Some("me.gregd.cineworld.frontend.Main"),
  scalaJSUseMainModuleInitializer in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    "com.github.japgolly.scalajs-react" %%% "core" % "1.0.0",
    "com.github.japgolly.scalajs-react" %%% "extra" % "1.0.0",
    "com.github.japgolly.scalacss" %%% "core" % "0.5.3",
    "com.github.japgolly.scalacss" %%% "ext-react" % "0.5.3",
    "org.scala-js" %%% "scalajs-java-time" % "0.2.0"
  ),
  jsDependencies ++= Seq(
    "org.webjars.bower" % "react" % "15.4.2" / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.bower" % "react" % "15.4.2" / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
    "org.webjars.bower" % "react" % "15.4.2" / "react-dom-server.js" minified "react-dom-server.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOMServer"
  ),
  (emitSourceMaps in fullOptJS) := true
).dependsOn(sharedJs)


lazy val server = project.settings(
  commonSettings,
  name := "fulfilmed",
  dockerRepository := Some("grogs"),
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  WebKeys.packagePrefix in Assets := "public/",
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % "2.12.4",
    "org.scala-lang" % "scala-compiler" % "2.12.4",
    "info.debatty" % "java-string-similarity" % "1.0.0",
    "org.scalaj" %% "scalaj-http" % "2.3.0",
    "org.json4s" %% "json4s-native" % "3.5.3",
    "org.json4s" %% "json4s-jackson" % "3.5.3",
    "com.google.code.findbugs" % "jsr305" % "3.0.2",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.lihaoyi" %% "scalatags" % "0.6.7",
    "org.typelevel" %% "cats" % "0.9.0",
    "io.monix" %% "monix" % "2.3.0",
    "com.vmunier" %% "scalajs-scripts" % "1.1.1",
    "com.github.cb372" %% "scalacache-memcached" % "0.10.0",
    ws,
    filters,
    guice,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
    "org.scalatest" %% "scalatest" % "3.0.4" % Test,
    "com.lihaoyi" %% "pprint" % "0.5.3" % Test,
    "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test,
    "com.typesafe.akka" %% "akka-http" % "10.0.10" % Test
  ),
  libraryDependencies ++= Seq(
    "org.webjars" %% "webjars-play" % "2.6.2",
    "org.webjars" % "font-awesome" % "4.7.0"
  ),
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  deploy := {
    import scala.sys.process._

    val v = version.value
    val image = (dockerAlias in Docker).value.versioned
    val app = "fulfilmed"
    val instances = (0 to 1).toList

    val pull = s"docker pull $image"

    def stop(instance: Int) = s"docker stop $app.$instance"

    def remove(instance: Int) = s"docker rm $app.$instance"

    def create(instance: Int) = {
      val exposeTo = "900" + instance
      s"docker run -d --name $app.$instance -p $exposeTo:9000 $image"
    }

    def deploy(instance: Int) = List(stop(instance), remove(instance), create(instance)).mkString(" && ")

    val deployAllInstances = instances.map(deploy).mkString(" && ")

    val deployCmd = s"$pull && $deployAllInstances"

    val log = streams.value.log

    log.success(s"Deploying with this cmd:\n$deployCmd")

    val status = Process("ssh", Seq("root@fulfilmed.com", deployCmd)).!

    if (status == 0) {
      log.success(s"successfully deployed $image.")
    } else {
      log.error(s"Deployment failed with status code $status.")
      throw new IllegalArgumentException("Deploy failed.")
    }
  }
)
  .enablePlugins(PlayScala, GitVersioning)
  .disablePlugins(PlayLayoutPlugin)
  .dependsOn(sharedJvm)

lazy val shared = crossProject.crossType(CrossType.Pure).settings(
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "upickle" % "0.4.4",
    "com.lihaoyi" %%% "autowire" % "0.2.6",
    "com.lihaoyi" %%% "scalatags" % "0.6.7"
  ),
  commonSettings
).jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

onLoad in Global ~= (_ andThen ("project server" :: _))