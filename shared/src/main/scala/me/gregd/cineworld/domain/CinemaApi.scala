package me.gregd.cineworld.domain

import scala.concurrent.Future

trait CinemaApi {

  def getMoviesAndPerformances(cinemaId: String, date: String): Future[Map[Movie, List[Performance]]]

}
