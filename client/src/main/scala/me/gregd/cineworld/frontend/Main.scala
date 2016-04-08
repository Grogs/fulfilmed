package me.gregd.cineworld.frontend

import japgolly.scalajs.react.ReactDOM
import me.gregd.cineworld.domain.CinemaApi
import org.scalajs.dom._

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import autowire._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue


@JSExport
object Main extends JSApp {
  @JSExport
  def main(): Unit = {
    console.log("ScalaJS App loaded")
  }

  @JSExport
  def films(): Unit = {

    def render(state: FilmsState = FilmsState(true, Map.empty)) =
      ReactDOM.render(
        views.FilmPage(state),
        document.getElementById("content")
      )

    render()

    for {
      res <- Client[CinemaApi].getMoviesAndPerformances("66", "tomorrow").call()
    } render(FilmsState(isLoading = false, res))

//    setInterval(() => render(FilmsState(false, Map.empty)), 3000)

  }

}