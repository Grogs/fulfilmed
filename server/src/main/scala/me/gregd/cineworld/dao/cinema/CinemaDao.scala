package me.gregd.cineworld.dao.cinema

import java.time.LocalDate

import com.google.inject.ImplementedBy
import me.gregd.cineworld.dao.cinema.cineworld.CineworldCinemaDao
import me.gregd.cineworld.domain.{Cinema, Movie, Performance}

import scala.concurrent.Future

@ImplementedBy(classOf[CineworldCinemaDao])
trait CinemaDao {
  def retrieveCinemas(): Future[Seq[Cinema]]
  def retrieveMoviesAndPerformances(cinemaId: String, date: LocalDate): Future[Map[Movie, List[Performance]]]
}
