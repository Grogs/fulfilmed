package me.gregd.cineworld.pages

import scalatags.Text.all._
import scalatags.Text.tags2.{title => titleElem}
object Index {
  def apply(scriptPaths: List[String]) =
    html(
      head(
        titleElem("Fulfilmed"),
        link(rel:="stylesheet", href:="/assets/lib/font-awesome/css/font-awesome.min.css"),
        link(rel:="stylesheet", href:="/styles/index.css"),
        link(rel:="stylesheet", href:="/styles/films.css"),
      ),
      body(
        style := "margin: 0; background-color: #111;",
        div(id := "content"),
        for (p <- scriptPaths) yield script(`type` := "text/javascript", src := p)
      )
    )

}
