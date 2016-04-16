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

    ReactDOM.render(
      views.FilmPage(()),
      document.getElementById("content")
    )

  }

}