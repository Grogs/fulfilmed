package me.gregd.cineworld.pages

import scalatags.Text.all._
import scalatags.Text.tags2.{title=>titleElem}

class Index {

  def apply(scalaJsPath: String) =
    html(
      head(
        titleElem("Fulfilmed")
      ),
      body(
        script(`type`:="text/javascript", src:=scalaJsPath),
        script("Main().main()")
      )
    )

}

object Index extends Index
