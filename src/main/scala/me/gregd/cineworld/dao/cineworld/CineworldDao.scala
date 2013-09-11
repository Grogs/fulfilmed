package me.gregd.cineworld.dao.cineworld

import me.gregd.cineworld.domain.{Cinema, Movie, Performance}
import me.gregd.cineworld.dao.imdb.IMDbDao

/**
 * Author: Greg Dorrell
 * Date: 09/06/2013
 */
trait CineworldDao {
  def getCinemas(): List[Cinema]
  def getMovies(cinema:String)(implicit imdb: IMDbDao): List[Movie]
  def getPerformances(movie:String): List[Performance]
}
