package me.gregd.cineworld

import javax.inject.Inject

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.domain.Movie
import play.api.libs.json.Json
import play.api.mvc.{Action, InjectedController}
import play.api.mvc.Results.Ok
import play.mvc.Controller

import scala.concurrent.ExecutionContext.Implicits.global

class DebugController @Inject()(tmdb: TheMovieDB, movies: Movies) extends InjectedController with LazyLogging {

  implicit val movieFormat = Json.format[Movie]

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

}