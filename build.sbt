import com.decodified.scalassh.{HostConfig, PublicKeyLogin, SSH, SshLogin}
import com.decodified.scalassh.HostKeyVerifiers.DontVerify
import webscalajs.SourceMappings

lazy val commonSettings = Seq(
  organization := "me.gregd",
  version := "1.2",
  scalaVersion := "2.11.8"
)

lazy val client = project.enablePlugins(ScalaJSPlugin, ScalaJSWeb).settings(
  commonSettings,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    "com.github.japgolly.scalajs-react" %%% "core" % "0.11.1",
    "com.github.japgolly.scalajs-react" %%% "extra" % "0.11.1",
    "com.github.japgolly.scalacss" %%% "core" % "0.4.0",
    "com.github.japgolly.scalacss" %%% "ext-react" % "0.4.0",
    "org.scala-js" %%% "scalajs-java-time" % "0.2.0"
  ),
  jsDependencies ++= Seq(
    "org.webjars.bower" % "react" % "15.0.1" / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.bower" % "react" % "15.0.1" / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM"
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
  managedClasspath in Runtime += (packageBin in Assets).value,
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % "2.11.7",
    "org.scala-lang" % "scala-compiler" % "2.11.7",
    "com.rockymadden.stringmetric" % "stringmetric-core_2.11" % "0.27.4",
    "com.chuusai" % "shapeless_2.11" % "2.2.4",
    "com.typesafe.slick" % "slick_2.11" % "2.1.0",
    "com.h2database" % "h2" % "1.3.164",
    "org.scalaj" % "scalaj-http_2.11" % "1.1.5",
    "org.json4s" % "json4s-native_2.11" % "3.2.11",
    "org.json4s" % "json4s-jackson_2.11" % "3.2.11",
    "org.scalatra" % "scalatra_2.11" % "2.3.1",
    "org.scalatra" % "scalatra-json_2.11" % "2.3.1",
    "com.google.guava" % "guava" % "16.0.1",
    "com.google.code.findbugs" % "jsr305" % "3.0.1",
    "com.typesafe.scala-logging" % "scala-logging-slf4j_2.11" % "2.1.2",
    "ch.qos.logback" % "logback-classic" % "1.0.13",
    "org.jsoup" % "jsoup" % "1.7.3",
    "org.feijoas" % "mango_2.11" % "0.11", // exclude("jsr305"),
    "com.lihaoyi" %% "scalatags" % "0.5.4",
    "org.typelevel" %% "cats" % "0.9.0",
    ws,
    filters,
    "org.scalatra" % "scalatra-scalatest_2.11" % "2.3.1" % "test",
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    "org.scalatest" % "scalatest_2.11" % "1.9.1" % "test",


    "com.vmunier" %% "scalajs-scripts" % "1.0.0"

  ),
  libraryDependencies ++= Seq(
    "org.webjars" %% "webjars-play" % "2.4.0-1",
    "org.webjars" % "font-awesome" % "4.5.0"
  ),
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value
)
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .dependsOn(sharedJvm)

lazy val shared = crossProject.crossType(CrossType.Pure).settings(
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "upickle" % "0.3.6",
    "com.lihaoyi" %%% "autowire" % "0.2.4",
    "com.lihaoyi" %%% "scalatags" % "0.5.2",
    "fr.hmil" %%% "roshttp" % "1.0.0"
  ),
  commonSettings
).jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value


lazy val dokkuHost = settingKey[String]("Host for Dokku deployment")
lazy val dokkuUser = settingKey[String]("Remote Dokku user (normally 'dokku')")
lazy val dokkuApp = settingKey[String]("Your Dokku app name")
lazy val dokkuVersion = settingKey[String]("Dokku app version - defaults to project version")
lazy val dokkuLogin = settingKey[SshLogin]("SSH Login for Dokku deployment")
lazy val dokkuHostConfig = settingKey[HostConfig]("Host config for Dokku deployment")
lazy val dokkuDeploy = taskKey[Unit]("Deploy docker image with dokku")



dokkuUser := "root"
dokkuVersion := version.value
dokkuLogin := PublicKeyLogin(dokkuUser.value)

dokkuHostConfig := HostConfig(dokkuLogin.value)

dokkuApp := "fulfilmed"

dokkuHost := "fulfilmed.com"

dokkuDeploy <<= (dokkuHost, dokkuHostConfig, dokkuApp, dokkuVersion, target in Docker, streams) map {
  (host, hostConfig, app, version, dockerTag, streams) =>
    import streams.log

    val tagCmd = s"echo docker tag $dockerTag dokku/$app:$version"
    val deployCmd = s"echo dokku tags:deploy $app $version"

    SSH(host, hostConfig) { client =>

      def exec(tagCmd: String) = {
        val res = client.execPTY(tagCmd)
        res match {
          case Left(failure) =>
            log.error(failure)
          case Right(success) =>
            println(success.stdOutAsString())
        }
        res.right
      }

      val res = for {
        _ <- Right(log.info("Tagging docker image")).right
        _ <- exec(tagCmd)
        _ <- Right(log.info("Deploying")).right
        _ <- exec(deployCmd)
      } yield ()

      res
    }
    ()
}





