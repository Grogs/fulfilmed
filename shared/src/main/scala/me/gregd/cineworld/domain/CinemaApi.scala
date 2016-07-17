package me.gregd.cineworld.domain

import scala.concurrent.Future

trait CinemaApi {

  type Chain = String
  type Grouping = String

  def getCinemas(): Future[Seq[(Chain, Map[Grouping, Seq[Cinema]])]]

  def getMoviesAndPerformances(cinemaId: String, date: String): Future[Map[Movie, List[Performance]]]

}
