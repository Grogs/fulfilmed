package me.gregd.cineworld.frontend

import me.gregd.cineworld.frontend.components.{FilmsPage, IndexPage}
import me.gregd.cineworld.frontend.util._
import org.scalajs.dom._
import slinky.web.ReactDOM

object Main {

  def main(): Unit = {

    ReactDOM.render(
      Router(History.createBrowserHistory())(
        Switch(
          Route("/", IndexPage, exact = Some(true)),
          Route("/index", IndexPage, exact = Some(true)),
          Route("/films/:cinemaId/:date", FilmsPage, exact = Some(true)),
        )
      ),
      document.getElementById("content")
    )

  }

}
