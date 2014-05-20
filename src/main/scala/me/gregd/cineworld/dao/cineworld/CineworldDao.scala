package me.gregd.cineworld.dao.cineworld

import me.gregd.cineworld.domain.{Performance, Cinema, Movie}
import me.gregd.cineworld.dao.movies.MovieDao

trait CineworldDao {
  def getCinemas(): List[Cinema]
  def getMovies(cinema:String)(implicit imdb: MovieDao): List[Movie]
  def getPerformances(cinema: String): Map[String, Option[Seq[Performance]]]
}
