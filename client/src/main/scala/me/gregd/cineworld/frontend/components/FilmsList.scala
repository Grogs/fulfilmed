package me.gregd.cineworld.frontend.components

import me.gregd.cineworld.domain.model.{Movie, Performance}
import me.gregd.cineworld.frontend.components.Sort.Sort
import me.gregd.cineworld.frontend.styles.FilmsStyle
import me.gregd.cineworld.frontend.util.Loadable
import me.gregd.cineworld.frontend.util.Loadable.{Loaded, Loading, Unloaded}
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.web.html._

import scala.scalajs.js.Dynamic

@react class FilmsList extends StatelessComponent {

  case class Props(listings: Loadable[Map[Movie, Seq[Performance]]], sort: Sort)

  val filmCard = (m: Movie, pl: Seq[Performance]) => {
    def tmdbLink(m: Movie) = m.tmdbRating.map(div(className := FilmsStyle.tmdb)(_))

    def rtLink(m: Movie) = m.rottenTomatoes.map(
      div(className := FilmsStyle.rtAudience)(_)
    )

    def imdbLink(m: Movie) = {
      for {
        rating <- m.rating
        id <- m.imdbId
      } yield div(className := FilmsStyle.imdb)(a(href := s"http://www.imdb.com/title/$id")(rating))
    }

    val titleStyle = if (m.title.length > 20) className := FilmsStyle.longFilmTitle else className := FilmsStyle.filmTitle

    div(className := FilmsStyle.filmCard, key := m.title)(
      div(className := FilmsStyle.filmInfo)(
        div(className := (if (m.format.contains("3D")) FilmsStyle.threedee else ""))(
          div(titleStyle)(
            m.title
          ),
          div(className := FilmsStyle.ratings)(
            imdbLink(m),
            tmdbLink(m),
            rtLink(m)
          ),
          div(className := FilmsStyle.times)(
            for (p <- pl.toVector)
              yield
                a(href := p.booking_url, key :=p.booking_url)(
                  div(className := FilmsStyle.time)(
                    p.time
                  )
                )
          )
        )
      ),
      img(className := FilmsStyle.filmBackground, src := m.posterUrl.get)
    )
  }

  def render() = {
    def icon(faClasses: String, message: String) = {
      val iconStyle = Dynamic.literal(
        margin = "50px 0 50px 0",
        color = "white",
        textAlign = "center"
      )
      div(style := iconStyle)(
        i(className := s"fa $faClasses fa-5x"),
        div(className := FilmsStyle.label)(
          message
        )
      )
    }

    props.listings match {
      case Unloaded =>
        icon("fa-frown-o", "An error occurred. Please check back later.")
      case Loading =>
        icon("fa-refresh fa-spin", "Loading movies")
      case Loaded(listings) =>
        if (listings.isEmpty) {
          icon("fa-frown-o", "No movies found!")
        } else {
          div(className := FilmsStyle.filmListContainer)(
            for (m <- listings.toSeq.sorted(props.sort.ordering)) yield filmCard.tupled(m)
          )
        }
    }
  }
}
