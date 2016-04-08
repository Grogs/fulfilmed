package me.gregd.cineworld

import javax.inject.Inject

import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cineworld.Cineworld
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain.{CinemaApi, Movie, Performance}
import org.joda.time.LocalDate
import play.api.Environment

class CinemaService @Inject() (env: Environment, dao: Cineworld, implicit val movies: MovieDao, implicit val  tmdb: TheMovieDB) extends CinemaApi{

  def getDate(s: String): LocalDate = s match {
    case "today" => new LocalDate
    case "tomorrow" => new LocalDate() plusDays 1
    case other => new LocalDate(other)
  }

  def getMoviesAndPerformances(cinemaId: String, dateRaw: String): Map[Movie, List[Performance]] = {
    val date = getDate(dateRaw)
    val allPerformances = dao.retrievePerformances(cinemaId, date)
    (for {
      movie <- dao.retrieveMovies(cinemaId, date)
      id = movie.cineworldId orElse movie.imdbId getOrElse movie.title
      performances = allPerformances.getOrElse(id, None).getOrElse(Nil)
    } yield movie -> performances.toList).toMap
  }

}
