package me.gregd.cineworld.domain.model

case class Movie (
  title: String,
  cineworldId: Option[String] = None,
  format: Option[String] = None,
  imdbId: Option[String] = None,
  tmdbId: Option[String] = None,
  rating: Option[Double] = None,
  votes: Option[Int] = None,
  tmdbRating: Option[Double] = None,
  tmdbVotes: Option[Int] = None,
  posterUrl: Option[String] = None,
  metascore: Option[Int] = None,
  rottenTomatoes: Option[String] = None
)