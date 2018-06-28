package me.gregd.cineworld.domain

import java.time.LocalDate

import me.gregd.cineworld.domain.model.{Film, Performance}

import scala.concurrent.Future

trait CinemaDao {
  def retrieveCinemas(): Future[Seq[Cinema]]
  def retrieveMoviesAndPerformances(cinemaId: String, date: LocalDate): Future[Map[Film, Seq[Performance]]]
}
