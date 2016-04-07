package me.gregd.cineworld.pages

import scalatags.Text.all._
import scalatags.Text.tags2.{title=>titleElem}

class Index {

  def apply(scriptPaths: List[String]) =
    html(
      head(
        titleElem("Fulfilmed")
      ),
      body(
        for (p <- scriptPaths) yield script(`type`:="text/javascript", src:=p),
        script("Main().main()")
      )
    )

}

object Index extends Index
