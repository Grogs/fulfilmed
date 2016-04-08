package me.gregd.cineworld.frontend

import japgolly.scalajs.react.ReactDOM
import org.scalajs.dom._

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

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

    setInterval(() => render(FilmsState(false, Map.empty)), 3000)

  }

}