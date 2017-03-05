package me.gregd.cineworld.dao.cineworld

import com.google.inject.ImplementedBy
import me.gregd.cineworld.domain.{Cinema, Movie, Performance}
import me.gregd.cineworld.dao.movies.MovieDao
import org.joda.time.LocalDate

import scala.concurrent.Future

@ImplementedBy(classOf[CachingCinemaDao])
trait CinemaDao {
  def retrieveCinemas(): Future[Seq[Cinema]]
  def retrieveMoviesAndPerformances(cinemaId: String, dateRaw: String): Future[Map[Movie, List[Performance]]]
}
