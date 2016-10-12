package me.gregd.cineworld.frontend

import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.extra.router.{BaseUrl, Redirect, Router, RouterConfigDsl}
import japgolly.scalajs.react._, vdom.prefix_<^._

import me.gregd.cineworld.frontend.components.{FilmsStyle, IndexPage, IndexStyle}
import org.scalajs.dom._

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._


@JSExport
object Main extends JSApp {

  @JSExport
  def main(): Unit = {

    val baseUrl = BaseUrl.fromWindowOrigin + "/"
    IndexStyle.addToDocument()
    FilmsStyle.addToDocument()

    val routerConfig = RouterConfigDsl[Page].buildConfig{ dsl =>
      import dsl._
      (removeTrailingSlashes
      |staticRoute(root, Home) ~> renderR( IndexPage(_)() )
      |dynamicRouteCT("#!/films" / string("[0-9]+").caseClass[Films]) ~> dynRender( components.FilmPageComponent(_) )
      ).notFound(redirectToPage(Home)(Redirect.Replace))
    }
    val router = Router(baseUrl, routerConfig)

    ReactDOM.render(router(), document.getElementById("content"))
  }

}
