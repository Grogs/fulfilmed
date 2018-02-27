package me.gregd.cineworld.frontend.components.film

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.TagMod.Composite
import japgolly.scalajs.react.vdom.html_<^._
import me.gregd.cineworld.domain.{Movie, Performance}
import me.gregd.cineworld.frontend.components.film.Sort.Sort
import me.gregd.cineworld.frontend.styles.FilmsStyle

import scala.language.implicitConversions

object FilmListComponent {
  val filmCard = (m: Movie, pl: Seq[Performance]) => {
    val titleStyle = if (m.title.length > 20) ^.`class` := FilmsStyle.longFilmTitle else ^.`class` := FilmsStyle.filmTitle
    <.div(^.`class` := FilmsStyle.filmCard,
      <.div(^.`class` := FilmsStyle.filmInfo,
        <.div(^.classSet(FilmsStyle.threedee -> m.format.contains("3D")),
          <.div(titleStyle, m.title),
          <.div(^.`class` := FilmsStyle.ratings, imdbLink(m), tmdbLink(m), rtLink(m)),
          <.div(^.`class` := FilmsStyle.times,
            Composite(for (p <- pl.toVector) yield
              <.a(^.href := p.booking_url,
                <.div(^.`class` := FilmsStyle.time, p.time)
              )
            ))
        )
      ),
      <.img(^.`class` := FilmsStyle.filmBackground, ^.src := m.posterUrl.get)
    )
  }

  def tmdbLink(m: Movie) = m.tmdbRating.whenDefined(<.div(^.`class` := FilmsStyle.tmdb, _))

  def rtLink(m: Movie) = m.rottenTomatoes.whenDefined(<.div(^.`class` := FilmsStyle.rtAudience, _))

  def imdbLink(m: Movie) = {
    for {
      rating <- m.rating
      id <- m.imdbId
    } yield <.div(^.`class` := FilmsStyle.imdb, <.a(^.href := s"http://www.imdb.com/title/$id", rating))
  }.whenDefined

  val FilmsList =
    ScalaComponent.builder[(Boolean, Sort, Map[Movie, Seq[Performance]])]("FilmsList").render_P {
      case (loading, sort, films) =>
        def icon(faClasses: String, message: String) = {
          <.div(^.margin := "50px 0 50px 0", ^.color.white, ^.textAlign.center,
            <.i(^.`class` := s"fa $faClasses fa-5x"),
            <.div(^.`class` := FilmsStyle.label, message)
          )
        }

        val spinner = icon("fa-refresh fa-spin", "Loading movies")

        val frown = icon("fa-frown-o", "No movies found!")

        val movies = films.toVector.sorted(sort.ordering)
        if (movies.isEmpty)
          if (loading) spinner else frown
        else
          <.div(
            ^.`class` := FilmsStyle.filmListContainer,
            Composite(for (m <- movies) yield filmCard.tupled(m))
          )
    }.build
}
