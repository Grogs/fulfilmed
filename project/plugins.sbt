resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

val playVersion = "2.4.0"

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.0")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % playVersion)
