package me.gregd.cineworld.domain.service

import me.gregd.cineworld.domain.model.{Movie, Performance}

import scala.concurrent.Future

trait Listings[F[_]] {
  def getMoviesAndPerformancesFor(cinemaId: String, date: String): F[Seq[(Movie, Seq[Performance])]]
}
