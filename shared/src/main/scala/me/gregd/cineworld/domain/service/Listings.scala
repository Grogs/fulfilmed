package me.gregd.cineworld.domain.service

import me.gregd.cineworld.domain.model.{Movie, Performance}

import scala.concurrent.Future

trait Listings {
  def getMoviesAndPerformancesFor(cinemaId: String, date: String): Future[Seq[(Movie, Seq[Performance])]]
}
