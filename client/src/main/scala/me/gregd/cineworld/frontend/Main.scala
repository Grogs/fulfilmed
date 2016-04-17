package me.gregd.cineworld.frontend

import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.extra.router.{BaseUrl, Redirect, Router, RouterConfigDsl}
import org.scalajs.dom._
import japgolly.scalajs.react.vdom.prefix_<^._
import me.gregd.cineworld.frontend.components.IndexPage
import org.scalajs.dom

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport


@JSExport
object Main extends JSApp {
  @JSExport
  def main(): Unit = {
    val baseUrl = BaseUrl.fromWindowOrigin + "/"
    val routerConfig = RouterConfigDsl[Page].buildConfig{ dsl =>
      import dsl._
      (emptyRule
      |staticRoute(root, Home) ~> render( IndexPage() )
      |dynamicRouteCT("#!/films" / string("[0-9]+").caseClass[Films]) ~> render( components.FilmPageComponent() )
      ).notFound(redirectToPage(Home)(Redirect.Replace))
    }
    val router = Router(baseUrl, routerConfig.logToConsole)
    ReactDOM.render(router(), document.getElementById("content"))
  }

  @JSExport
  def films(): Unit = {

    ReactDOM.render(
      components.FilmPageComponent(),
      document.getElementById("content")
    )

  }

}