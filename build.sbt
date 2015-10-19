lazy val commonSettings = Seq(
  organization := "me.gregd",
  version := "1.1",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "fulfilmed-backend"
  ).
  enablePlugins(PlayScala).
  disablePlugins(PlayLayoutPlugin)

routesGenerator := InjectedRoutesGenerator

//mainClass in (Compile,run) := Some("me.gregd.cineworld.util.JettyBootstrap")

resolvers += "Sonatype OSS Releases" at  "http://oss.sonatype.org/content/groups/public"

resolvers += "mandubian-snapshots" at  "http://github.com/mandubian/mandubian-mvn/raw/master/snapshots/"

resolvers += "mandubian-releases" at  "http://github.com/mandubian/mandubian-mvn/raw/master/releases/"

resolvers += "typesafe-releases" at  "http://repo.typesafe.com/typesafe/releases/"

resolvers += "sonatype-snapshots" at  "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "sonatype-releases" at  "http://oss.sonatype.org/content/repositories/releases"

resolvers += "eclipse" at  "http://download.eclipse.org/rt/eclipselink/maven.repo"

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
  "org.eclipse.jetty" % "jetty-webapp" % "8.1.10.v20130312",
  "org.scalatra" % "scalatra_2.11" % "2.3.1",
  "org.scalatra" % "scalatra-json_2.11" % "2.3.1",
  "com.google.guava" % "guava" % "14.0",
  "com.google.code.findbugs" % "jsr305" % "1.3.7",
  "com.typesafe.scala-logging" % "scala-logging-slf4j_2.11" % "2.1.2",
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "org.jsoup" % "jsoup" % "1.7.3",
  "org.feijoas" % "mango_2.11" % "0.11",// exclude("jsr305"),
  "org.scalatra" % "scalatra-scalatest_2.11" % "2.3.1" % "test",
  "org.scalatest" % "scalatest_2.11" % "1.9.1" % "test"
)


