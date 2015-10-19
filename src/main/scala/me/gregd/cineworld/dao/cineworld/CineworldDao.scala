package me.gregd.cineworld.dao.cineworld

import me.gregd.cineworld.domain.{Performance, Cinema, Movie}
import me.gregd.cineworld.dao.movies.MovieDao
import org.joda.time.LocalDate

trait CineworldDao {
  def retrieveCinemas(): List[Cinema]
  def retrieveMovies(cinema:String, date: LocalDate)(implicit imdb: MovieDao): List[Movie]
  def retrievePerformances(cinema: String, date: LocalDate): Map[String, Option[Seq[Performance]]]
}
