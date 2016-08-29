package me.gregd.cineworld.dao.cineworld

import com.google.inject.ImplementedBy
import me.gregd.cineworld.domain.{Cinema, Movie, Performance}
import me.gregd.cineworld.dao.movies.MovieDao
import org.joda.time.LocalDate

import scala.concurrent.Future

@ImplementedBy(classOf[CachingCinemaDao])
trait CinemaDao {
  def retrieveCinemas(): Future[List[Cinema]]
  def retrieveMovies(cinema:String, date: LocalDate = new LocalDate()): Future[List[Movie]]
  def retrievePerformances(cinema: String, date: LocalDate = new LocalDate()): Future[Map[String, Option[Seq[Performance]]]]
}
