package me.gregd.cineworld.frontend

import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.extra.router.RouterConfigDsl
import org.scalajs.dom._
import japgolly.scalajs.react.vdom.prefix_<^._


import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport


@JSExport
object Main extends JSApp {
  @JSExport
  def main(): Unit = {
//    def routerConfig = RouterConfigDsl[Page].buildConfig{ dsl =>
//      import dsl._
//      staticRoute(root, Home) ~> render( <.h1("Welcome!") )
//
//    }
  }

  @JSExport
  def films(): Unit = {

    ReactDOM.render(
      components.FilmPageComponent(),
      document.getElementById("content")
    )

  }

}