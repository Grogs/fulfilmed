package me.gregd.cineworld.frontend.components

import autowire._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.TagMod.Composite
import japgolly.scalajs.react.vdom.html_<^._
import me.gregd.cineworld.domain.{Cinema, CinemaApi}
import me.gregd.cineworld.frontend.components.film.FilmPageComponent.Today
import me.gregd.cineworld.frontend.{Client, Films, Page}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalacss.ScalaCssReact.scalacssStyleaToTagMod

object IndexPage {
  val label = "label".reactAttr

  case class State(cinemas: Map[String, Map[String, Seq[Cinema]]])

  type Props = RouterCtl[Page]

  def apply(router: Props): Unmounted[Props, State, Backend] = component(router)

  val component = ScalaComponent
    .builder[Props]("IndexPage")
    .initialState(State(Map.empty))
    .renderBackend[Backend]
    .componentDidMount(_.backend.loadCinemas())
    .build

  class Backend($ : BackendScope[Props, State]) {

    def loadCinemas() = Callback.future {
      for {
        cinemas <- Client[CinemaApi].getCinemas().call()
      } yield $.setState(State(cinemas.toMap))
    }

    def selectCinema(e: ReactEventFromInput) = {
      val cinemaId = e.target.value
      for {
        p <- $.props
        _ <- p.set(Films(cinemaId, Today))
      } yield ()
    }

    def render(state: State) = {

      val cinemaDropdowns = Composite(
        for {
          (typ, cinemas) <- state.cinemas.toVector
        } yield
          <.div(
            <.select(
              IndexStyle.selectWithOffset,
              ^.id := "cinemas",
              ^.`class` := ".flat",
              ^.onChange ==> selectCinema,
              <.option(^.value := "?", ^.selected := "selected", ^.disabled := true, typ),
              Composite(for { (groupName, cinemas) <- cinemas.toVector.reverse } yield
                <.optgroup(label := groupName, Composite(for (cinema <- cinemas.toVector) yield <.option(^.value := cinema.id, cinema.name))))
            )
          ))

      <.div(
        ^.id := "indexPage",
        <.div(IndexStyle.top, <.div(IndexStyle.title, "Fulfilmed"), <.div(IndexStyle.blurb, "A better way to find the best films at your local UK cinema"), cinemaDropdowns),
        <.div(
          IndexStyle.description,
          ^.id := "description",
          <.h3("What?"),
          <.p(
            "Fulfilmed lets you view the movies airing at your local cinema. It adds some features over your Cinema's standard website; inline movie ratings, sorting/filtering by rating, mark films as watched."),
          <.h3("Why?"),
          <.p(
            "I want to easily, at a glance, figure out which film to go see at the cinema. I can't do that with my cinema's website - I have to google/research each film individually. "),
          <.h3("What's next?"),
          <.p("Currently only Cineworld and Odeon cinemas are supported; I may add more. Beyond that, I want to receive notifications about upcoming high rated films.")
        )
      )
    }

  }

}
