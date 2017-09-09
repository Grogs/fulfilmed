resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.2")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.15")
addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.3")

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC3")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")