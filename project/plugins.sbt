resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

val playVersion = "2.4.0"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.0")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % playVersion)

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.7")

addSbtPlugin("com.vmunier" % "sbt-play-scalajs" % "0.2.10")
