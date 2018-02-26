package me.gregd.cineworld.domain

import java.time.LocalDate

import scala.concurrent.Future

trait CinemaApi {

  type Chain = String
  type Grouping = String

  def getCinemasGrouped(): Future[Map[Chain, Map[Grouping, Seq[Cinema]]]]
  def getCinemas(): Future[Seq[Cinema]]

  def getNearbyCinemas(coordinates: Coordinates): Future[Seq[Cinema]]

  def getMoviesAndPerformancesFor(cinemaId: String, date: String): Future[Map[Movie, List[Performance]]]
}

trait TypesafeCinemaApi extends CinemaApi {
  def getMoviesAndPerformances(cinemaId: String, date: LocalDate): Future[Map[Movie, List[Performance]]]
}
