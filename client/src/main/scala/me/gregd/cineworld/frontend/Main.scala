package me.gregd.cineworld.frontend

import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import me.gregd.cineworld.frontend.components.film.FilmPageComponent.{Date, Props, Today, Tomorrow}
import me.gregd.cineworld.frontend.components.film.{FilmPageComponent, FilmsStyle}
import me.gregd.cineworld.frontend.components.{IndexPage, IndexStyle}
import org.scalajs.dom._

import scala.scalajs.js.JSApp
import scalacss.Defaults._

object Main {

  def main(): Unit = {

    val baseUrl = BaseUrl.fromWindowOrigin + "/"

    var t0 = window.performance.now()
    IndexStyle.addToDocument()
    FilmsStyle.addToDocument()
    var t1 = window.performance.now()
    console.log(s"CSS rendering duration: ${t1 - t0}ms")

    val router = new Wiring(baseUrl).router

    router().renderIntoDOM(document.getElementById("content"))
  }

}
