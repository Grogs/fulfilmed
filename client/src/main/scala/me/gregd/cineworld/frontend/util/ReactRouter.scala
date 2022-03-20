package me.gregd.cineworld.frontend.util
//
//import slinky.core._
//import slinky.core.annotations.react
//import slinky.web.html.a
//
import scala.scalajs.js
//import scala.scalajs.js.annotation.JSImport
//
//@JSImport("react-router", JSImport.Default)
//@js.native
//object ReactRouter extends js.Object {
//  val StaticRouter: js.Object = js.native
//}
//
@js.native
trait RouteMatch extends js.Object {
  def params: js.Dictionary[String]
}

@js.native
trait RouteProps extends js.Object {
  def `match`: RouteMatch
  def history: History
}
//
//@JSImport("react-router-dom", JSImport.Default)
//@js.native
//object ReactRouterDOM extends js.Object {
//  val Router: js.Object = js.native
//  val BrowserRouter: js.Object = js.native
//  val Route: js.Object = js.native
//  val Switch : js.Object = js.native
//  val Link: js.Object = js.native
//  val Redirect: js.Object = js.native
//  val NavLink: js.Object = js.native
//}
//
//@react object StaticRouter extends ExternalComponent {
//  case class Props(location: String, context: js.Object)
//
//  override val component = ReactRouter.StaticRouter
//}
//
//@react object Router extends ExternalComponent {
//  case class Props(history: RichHistory)
//  override val component = ReactRouterDOM.Router
//}
//
//object BrowserRouter extends ExternalComponentNoProps {
//  override val component = ReactRouterDOM.BrowserRouter
//}
//
//object Switch extends ExternalComponentNoProps {
//  override val component = ReactRouterDOM.Switch
//}
//
//@react object Route extends ExternalComponent {
//  case class Props(path: String, component: ReactComponentClass[_], exact: Option[Boolean] = None)
//  override val component = ReactRouterDOM.Route
//}
//
//@react object Link extends ExternalComponentWithAttributes[a.tag.type] {
//  case class Props(to: String)
//  override val component = ReactRouterDOM.Link
//}
//
//@react object Redirect extends ExternalComponent {
//  case class Props(to: String, push: Option[Boolean] = None)
//  override val component = ReactRouterDOM.Redirect
//}
//
//@react object NavLink extends ExternalComponentWithAttributes[a.tag.type] {
//  case class Props(to: String, activeStyle: Option[js.Dynamic] = None, activeClassName: Option[String] = None)
//  override val component = ReactRouterDOM.NavLink
//}