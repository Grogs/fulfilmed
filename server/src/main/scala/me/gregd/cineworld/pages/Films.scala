package me.gregd.cineworld.pages

import scalatags.Text.all._
import scalatags.Text.tags2.title

class Films {
  def apply(scriptPaths: List[String]) =
    html(
      head(
        title("Fulfilmed"),
        link(rel:="stylesheet", href:="webjars/font-awesome/4.5.0/css/font-awesome.min.css")
      ),
      body(
        div( id:="content"),
        for (p <- scriptPaths) yield script(`type`:="text/javascript", src:=p ),
        script("me.gregd.cineworld.frontend.Main().films()")
      )
    )
}

object Films extends Films