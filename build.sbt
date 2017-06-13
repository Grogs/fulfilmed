import com.decodified.scalassh.{HostConfig, PublicKeyLogin, SSH, SshLogin}
import com.decodified.scalassh.HostKeyVerifiers.DontVerify
import webscalajs.SourceMappings
import ReleaseTransformations._

lazy val commonSettings = Seq(
  organization := "me.gregd",
  version := "1.6",
  scalaVersion := "2.11.11"
)

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")

lazy val client = project.enablePlugins(ScalaJSPlugin, ScalaJSWeb).settings(
  commonSettings,
  persistLauncher := true,
  persistLauncher in Test := false,
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
    "org.scala-lang" % "scala-library" % "2.11.11",
    "org.scala-lang" % "scala-compiler" % "2.11.11",
    "com.rockymadden.stringmetric" %% "stringmetric-core" % "0.27.4",
    "org.scalaj" %% "scalaj-http" % "1.1.5",
    "org.json4s" %% "json4s-native" % "3.2.11",
    "org.json4s" %% "json4s-jackson" % "3.2.11",
    "com.google.code.findbugs" % "jsr305" % "3.0.1",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
    "ch.qos.logback" % "logback-classic" % "1.0.13",
    "com.lihaoyi" %% "scalatags" % "0.5.4",
    "org.typelevel" %% "cats" % "0.9.0",
    "io.monix" %% "monix" % "2.2.2",
    "com.vmunier" %% "scalajs-scripts" % "1.0.0",
    "com.github.cb372" %% "scalacache-memcached" % "0.9.3",
    ws,
    filters,
    "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test,
    "org.scalatest" %% "scalatest" % "3.0.1" % Test,
    "com.lihaoyi" %% "pprint" % "0.4.3" % Test,
    "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test
  ),
  libraryDependencies ++= Seq(
    "org.webjars" %% "webjars-play" % "2.5.0",
    "org.webjars" % "font-awesome" % "4.5.0"
  ),
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value
)
  .enablePlugins(PlayScala, GitVersioning)
  .disablePlugins(PlayLayoutPlugin)
  .dependsOn(sharedJvm)

lazy val shared = crossProject.crossType(CrossType.Pure).settings(
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "upickle" % "0.4.3",
    "com.lihaoyi" %%% "autowire" % "0.2.6",
    "com.lihaoyi" %%% "scalatags" % "0.6.3",
    "fr.hmil" %%% "roshttp" % "1.0.0"
  ),
  commonSettings
).jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value



releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,   // performs the initial git checks
  tagRelease,
  ReleaseStep(releaseStepTask(publish in Docker)),
  setNextVersion,
  commitNextVersion,
  pushChanges             // also checks that an upstream branch is properly configured
)



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

      res.left.foreach(msg => throw new RuntimeException(msg))
    }.left.foreach(msg => throw new RuntimeException(msg))
}





