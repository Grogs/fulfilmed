package me.gregd.cineworld.integration.tmdb
import cats.effect.IO

trait TmdbService {
  val baseImageUrl: String
  def fetchMovies(): IO[Vector[TmdbMovie]]
  def fetchImdbId(tmdbId: String): IO[Option[String]]
  def alternateTitles(tmdbId: String): IO[Vector[String]]
}
