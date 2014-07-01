package me.gregd.cineworld.dao.cineworld

import me.gregd.cineworld.domain.{Performance, Cinema, Movie}
import me.gregd.cineworld.dao.movies.MovieDao
import org.joda.time.LocalDate

trait CineworldDao {
  def getCinemas(): List[Cinema]
  def getMovies(cinema:String, date: LocalDate)(implicit imdb: MovieDao): List[Movie]
  def getPerformances(cinema: String, date: LocalDate): Map[String, Option[Seq[Performance]]]
}
