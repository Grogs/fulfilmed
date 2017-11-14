package me.gregd.cineworld

import javax.inject.Inject

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.dao.ratings.Ratings
import me.gregd.cineworld.domain.{Cinema, CinemaApi, Coordinates, Movie}
import play.api.libs.json.Json
import play.api.mvc.InjectedController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DebugController @Inject()(tmdb: TheMovieDB, movies: Movies, ratingService: Ratings, cinemaApi: CinemaApi) extends InjectedController with LazyLogging {

  implicit val movieFormat = Json.format[Movie]
  implicit val coordinatesFormat = Json.format[Coordinates]
  implicit val cinemaFormat = Json.format[Cinema]

  def tmdbNowPlaying() = Action.async(
    tmdb.fetchNowPlaying().map( nowPlaying =>
      Ok(Json.toJson(nowPlaying))
    )
  )

  def allMovies() = Action.async(
    movies.allMoviesCached().map( allMovies =>
      Ok(Json.toJson(allMovies))
    )
  )

  def imdbId(tmdbId: String) = Action.async(
    tmdb.fetchImdbId(tmdbId).map( imdbId =>
      Ok(Json.toJson(imdbId))
    )
  )

  def ratings(imdbId: String) = Action.async(
    ratingService.fetchRatings(imdbId).map( r =>
      Ok(Json.toJson(r))
    )
  )

  def cinemas() = Action.async(
    cinemaApi.getCinemas().map( cinemas =>
      Ok(Json.toJson(cinemas))
    )
  )

  def warmup() = Action.async(
    Future.sequence(Seq(
      movies.allMoviesCached(),
      cinemaApi.getCinemas()
    )).map(_ => Ok("Movies and cinema caches are populated."))
  )
}