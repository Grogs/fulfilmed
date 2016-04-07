package me.gregd.cineworld.frontend

import japgolly.scalajs.react.{React, ReactDOM}
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
    val state = views.FilmsState(true, Map.empty)
    val filmList = views.FilmsList(state)
    ReactDOM.render(filmList, document.getElementById("films"))
    filmList.setState(state.copy(isLoading = false))
  }

}