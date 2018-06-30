package me.gregd.cineworld.frontend.components

import me.gregd.cineworld.domain.{Cinema, CinemasService}
import me.gregd.cineworld.frontend.services.Geolocation
import me.gregd.cineworld.frontend.Client
import me.gregd.cineworld.frontend.styles.IndexStyle
import org.scalajs.dom.raw.Event
import slinky.core._
import slinky.core.annotations.react
import slinky.web.html._
import autowire._
import me.gregd.cineworld.frontend.util.{Loadable, Redirect, RouteProps}
import me.gregd.cineworld.frontend.util.Loadable.{Loaded, Loading, Unloaded}
import org.scalajs.dom.html.Input
import slinky.core.facade.ReactElement

import scala.scalajs.js.Dynamic
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@react class IndexPage extends Component {

  type Props = RouteProps

  case class State(
      allCinemas: Loadable[Map[String, Map[String, Seq[Cinema]]]],
      nearbyCinemas: Loadable[Seq[Cinema]],
      redirect: Option[String]
  )

  def initialState = State(Unloaded, Unloaded, None)

  override def componentDidMount(): Unit = {
    Geolocation.havePermission().foreach(permissionGranted => loadNearbyCinemas())

    loadAllCinemas()
  }

  def loadAllCinemas() = {
    for {
      cinemas <- Client[CinemasService].getCinemasGrouped().call()
    } yield setState(_.copy(allCinemas = Loaded(cinemas)))
  }

  def loadNearbyCinemas(): Future[Unit] = {
    setState(_.copy(nearbyCinemas = Loading))
    for {
      userLocation <- Geolocation.getCurrentPosition()
      nearbyCinemas <- Client[CinemasService].getNearbyCinemas(userLocation).call()
    } yield setState(_.copy(nearbyCinemas = Loaded(nearbyCinemas)))
  }

  def render() = {

    def selectCinema(e: Event): Unit = {
      val cinemaId = e.target.asInstanceOf[Input].value
      setState(_.copy(redirect = Some(cinemaId)))
    }

    def cinemaSelect(typ: String, allCinemas: Map[String, Seq[Cinema]]) = {
      select(
        className := IndexStyle.select,
        id := "cinemas",
        onChange := (e => selectCinema(e)),
      )(
        option(value := "?", selected := true, disabled := true)(typ),
        allCinemas.toSeq.reverse.map {
          case (groupName, cinemas) =>
            optgroup(label := groupName)(
              for (cinema <- cinemas) yield option(value := cinema.id)(cinema.name)
            )
        }
      )
    }

    val cinemaDropdowns: ReactElement = state.allCinemas match {
      case Unloaded | Loading =>
        div(className := IndexStyle.blurb)("Loading")
      case Loaded(allCinemas) =>
        div(
          allCinemas.map {
            case (typ, cinemas) =>
              div(cinemaSelect(typ, cinemas))
          }
        )
    }

    val nearbyCinemas = div(
      state.nearbyCinemas match {
        case Unloaded =>
          button(className := IndexStyle.btn, onClick := (() => loadNearbyCinemas()))(
            "Load Nearby Cinemas"
          )
        case Loading =>
          val spinnerStyle = Dynamic.literal(
            color = "white",
            textAlign = "center"
          )
          div(style := spinnerStyle)(
            i(className := "fa fa-refresh fa-spin fa-5x")
          )
        case Loaded(cinemas) =>
          select(
            className := IndexStyle.select,
            id := "nearby-cinemas",
            onChange := (e => selectCinema(e))
          )(
            option(value := "?", selected := true, disabled := true)(
              "Select nearby cinema..."
            ),
            for (c <- cinemas.toVector) yield option(value := c.id)(c.name)
          )

      }
    )

    state.redirect match {
      case Some(cinemaId) =>
        Redirect(s"/films/$cinemaId/today", push = Some(true))
      case None =>
        div(id := "indexPage")(
          div(className := IndexStyle.top)(
            div(className := IndexStyle.title)(
              "Fulfilmed"
            ),
            div(className := IndexStyle.blurb)(
              "See films showing at your local cinema, with inline movie ratings and the ability to sort by rating."
            ),
            nearbyCinemas,
            cinemaDropdowns
          ),
          div(className := IndexStyle.description, id := "description")
        )
    }
  }
}
