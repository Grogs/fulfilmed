package me.gregd.cineworld.dao.cinema

import com.google.inject.ImplementedBy
import me.gregd.cineworld.dao.cinema.cineworld.CineworldCinemaDao
import me.gregd.cineworld.domain.{Cinema, Movie, Performance}

import scala.concurrent.Future

@ImplementedBy(classOf[CineworldCinemaDao])
trait CinemaDao {
  def retrieveCinemas(): Future[Seq[Cinema]]
  def retrieveMoviesAndPerformances(cinemaId: String, dateRaw: String): Future[Map[Movie, List[Performance]]]
}
