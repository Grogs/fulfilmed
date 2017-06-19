package me.gregd.cineworld.frontend.components.film

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.TagMod.Composite
import japgolly.scalajs.react.vdom.html_<^._
import me.gregd.cineworld.domain.{Movie, Performance}
import me.gregd.cineworld.frontend.components.film.Sort.Sort

import scala.language.implicitConversions
import scalacss.ScalaCssReact.scalacssStyleaToTagMod

object FilmListComponent {
  val filmCard = (m: Movie, pl: Seq[Performance]) =>
    <.div(FilmsStyle.filmCard,
      <.div(FilmsStyle.filmInfo,
        <.div(^.classSet("threedee" -> m.format.contains("3D")),
          <.div(FilmsStyle.filmTitle, m.title),
          <.div(FilmsStyle.ratings, imdbLink(m), tmdbLink(m), rtLink(m)),
          <.div(FilmsStyle.times,
            Composite(for (p <- pl.toVector) yield
              <.a(^.href := p.booking_url,
                <.div(FilmsStyle.time, p.time)
              )
            ))
        )
      ),
      <.img(FilmsStyle.filmBackground, ^.src := m.posterUrl.get)
    )

  def tmdbLink(m: Movie) = m.tmdbRating.whenDefined(<.div(FilmsStyle.tmdb, _))

  def rtLink(m: Movie) = m.rottenTomatoes.whenDefined(<.div(FilmsStyle.rtAudience, _))

  def imdbLink(m: Movie) = {
    for {
      rating <- m.rating
      id <- m.imdbId
    } yield <.div(FilmsStyle.imdb, <.a(^.href := s"http://www.imdb.com/title/tt$id", rating))
  }.whenDefined

  val FilmsList =
    ScalaComponent.builder[(Boolean, Sort, Map[Movie, Seq[Performance]])]("FilmsList").render_P {
      case (loading, sort, films) =>
        def icon(faClasses: String, message: String) = {
          <.div(^.margin := "50px 0 50px 0", ^.color.white, ^.textAlign.center,
            <.i(^.`class` := s"fa $faClasses fa-5x"),
            <.div(FilmsStyle.label, message)
          )
        }

        val spinner = icon("fa-refresh fa-spin", "Loading movies")

        val frown = icon("fa-frown-o", "No movies found!")

        val movies = films.toVector.sorted(sort.ordering)
        if (movies.isEmpty)
          if (loading) spinner else frown
        else
          <.div(
            FilmsStyle.filmListContainer,
            Composite(for (m <- movies) yield filmCard.tupled(m))
          )
    }.build
}
