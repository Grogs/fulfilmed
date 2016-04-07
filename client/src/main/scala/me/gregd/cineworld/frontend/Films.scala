package me.gregd.cineworld.frontend

import japgolly.scalajs.react.ReactComponentB
import me.gregd.cineworld.domain.{Movie,Performance}
import japgolly.scalajs.react.vdom.prefix_<^._


package object views {


  case class FilmsState(isLoading: Boolean, films: Map[Movie, List[Performance]])

  val FilmCard = {
    ReactComponentB[(Movie, List[Performance])]("FilmCard")
      .render_P{ case (m, pl) =>
        <.div( ^.className := "film-container",
          <.div( ^.classSet1("film", "threedee" -> (m.format == "3D")),
            <.div(^.`class` := "title", m.title),
            <.div(^.`class` := "ratings",
              <.div(^.`class` := "rating imdb", m.rating),
              <.div(^.`class` := "rating rt", m.criticRating),
              <.div(^.`class` := "rating rtAudience", m.audienceRating)
            ),
            <.div(^.`class` := "times",
              for (p <- pl) yield
                <.a(^.href := p.booking_url,
                  <.div(^.`class` := "time", p.time)
                )
            )
          ),
          <.img( ^.`class`:="film-background", ^.src := m.posterUrl)
        )
      }
      .build
  }

  val FilmsList = {
    def icon(faClasses: String, message: String) = {
      <.div(^.margin := "50px 0 50px 0", ^.color.white,
        <.i(^.`class` := s"fa $faClasses fa-5x"),
        <.div(^.`class`:="label", message)
      )
    }
    def spinner = icon("fa-refresh fa-spin", "Loading movies")
    def frown = icon("fa-frown-o", "No movies found!")
    ReactComponentB[FilmsState]("FilmsList")
    .initialState(FilmsState(true, Map.empty))
    .render_P{
      case FilmsState(loading, movies) =>
        if (loading)
          spinner
        else if (movies.isEmpty)
          frown
        else
          <.div( ^.className := "films-list-container",
            for (m <- movies) yield FilmCard(m)
          )
    }
    .build
  }

}
