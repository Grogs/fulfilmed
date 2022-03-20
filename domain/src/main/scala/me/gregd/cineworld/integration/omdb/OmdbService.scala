package me.gregd.cineworld.integration.omdb
import cats.effect.IO

trait OmdbService {

  def fetchRatings(imdbId: String): IO[RatingsResult]
}
