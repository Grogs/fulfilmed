package me.gregd.cineworld.frontend.components

import me.gregd.cineworld.domain.model.{Movie, MovieListing, Performance}

object Sort {
  type Entry = MovieListing

  sealed abstract class Sort(val key: String, val description: String, val ordering: Ordering[Entry])

  val all: Vector[Sort] = Vector(NextShowing, ImdbRating, TmdbRating, RottenTomatoes)

  case object NextShowing extends Sort("showtime", "Next Showing", Ordering.by { case MovieListing(_, performances) => if (performances.isEmpty) "99:99" else performances.map(_.time).min })

  case object ImdbRating extends Sort("imdb", "IMDb Rating (Descending)", Ordering.by { e: Entry => e.movie.rating.getOrElse(0.0) }.reverse)

  case object TmdbRating extends Sort("tmdb", "TheMovieDatabase Rating (Descending)", Ordering.by { e: Entry => e.movie.tmdbRating.getOrElse(0.0) }.reverse)

  case object RottenTomatoes extends Sort("rt", "Rotten Tomatoes Rating (Descending)", Ordering.by { e: Entry => e.movie.rottenTomatoes.getOrElse("0%") }.reverse)

}
