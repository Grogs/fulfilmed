package me.gregd.cineworld.frontend.components.film

import FilmPageComponent.Entry

object Sort {
  sealed abstract class Sort(val key: String, val description: String, val ordering: Ordering[Entry])

  val all: Vector[Sort] = Vector(NextShowing, ImdbRating, TmdbRating, RottenTomatoes)

  case object NextShowing extends Sort("showtime", "Next Showing", Ordering.by { case (_, performances) => if (performances.isEmpty) "99:99" else performances.map(_.time).min })
  case object ImdbRating extends Sort("imdb", "IMDb Rating (Descending)", Ordering.by { e: Entry => e._1.rating.getOrElse(0.0) }.reverse)
  case object TmdbRating extends Sort("tmdb", "TheMovieDatabase Rating (Descending)", Ordering.by { e: Entry => e._1.tmdbRating.getOrElse(0.0) }.reverse)
  case object RottenTomatoes extends Sort("rt", "Rotten Tomatoes Rating (Descending)", Ordering.by { e: Entry => e._1.rottenTomatoes.getOrElse("0%") }.reverse)
}
