package me.gregd.cineworld.frontend.components

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import me.gregd.cineworld.frontend.services.Geolocation
import me.gregd.cineworld.frontend.Client
import me.gregd.cineworld.frontend.styles.IndexStyle
import org.scalajs.dom.Event
import slinky.core._
import slinky.core.annotations.react
import slinky.web.html._
import me.gregd.cineworld.domain.model.Cinema
import me.gregd.cineworld.frontend.util.Loadable
import me.gregd.cineworld.frontend.util.Loadable.{Loaded, Loading, Unloaded}
import org.scalajs.dom.html.Select
import slinky.core.facade.ReactElement
import slinky.core.SyntheticEvent
import io.circe.generic.auto._
import slinky.reactrouter.Redirect

import scala.scalajs.js.Dynamic
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@react class IndexPage extends Component {

  type Props = Unit

  case class State(allCinemas: Loadable[Map[String, Map[String, Seq[Cinema]]]],
                   nearbyCinemas: Loadable[Seq[Cinema]],
                   redirect: Option[String])

  def initialState = State(Unloaded, Unloaded, None)

  override def componentDidMount(): Unit = {
    Geolocation
      .havePermission()
      .foreach(permissionGranted => loadNearbyCinemas().unsafeRunAndForget()(IORuntime.global))

    loadAllCinemas()
  }

  def loadAllCinemas() = {
    def isLondon(s: Cinema) =
      if (s.name startsWith "London - ") "London cinemas"
      else "All other cinemas"

    def group(cinemas: Seq[Cinema]) =
      cinemas.groupBy(_.chain).view.mapValues(_.groupBy(isLondon)).toMap

    for {
      cinemas <- Client.cinemas.getCinemas
    } yield setState(_.copy(allCinemas = Loaded(group(cinemas))))
  }

  def loadNearbyCinemas(): IO[Unit] = {
    for {
      _ <- IO(setState(_.copy(nearbyCinemas = Loading)))
      userLocation <- IO.fromFuture(IO(Geolocation.getCurrentPosition()))
      nearbyCinemas <- Client.nearbyCinemas.getNearbyCinemas(userLocation)
      _ <- IO(setState(_.copy(nearbyCinemas = Loaded(nearbyCinemas))))
    } yield ()
  }

  def render() = {

    def selectCinema(e: SyntheticEvent[Select, Event]): Unit = {
      val cinemaId = e.target.value
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
              for (cinema <- cinemas)
                yield option(value := cinema.id)(cinema.name)
            )
        }
      )
    }

    val cinemaDropdowns: ReactElement = state.allCinemas match {
      case Unloaded | Loading =>
        div(className := IndexStyle.blurb)("Loading")
      case Loaded(allCinemas) =>
        div(allCinemas.map {
          case (typ, cinemas) =>
            div(cinemaSelect(typ, cinemas))
        })
    }

    val nearbyCinemas = div(state.nearbyCinemas match {
      case Unloaded =>
        button(
          className := IndexStyle.btn,
          onClick := (() => loadNearbyCinemas().unsafeRunAndForget()(IORuntime.global))
        )("Load Nearby Cinemas")
      case Loading =>
        val spinnerStyle =
          Dynamic.literal(color = "white", textAlign = "center")
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

    })

    state.redirect match {
      case Some(cinemaId) =>
        Redirect(s"/films/$cinemaId/today", push = true)
      case None =>
        div(id := "indexPage")(
          div(className := IndexStyle.top)(
            div(className := IndexStyle.title)("Fulfilmed"),
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
