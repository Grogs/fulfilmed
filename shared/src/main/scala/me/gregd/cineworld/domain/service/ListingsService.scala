package me.gregd.cineworld.domain.service

import me.gregd.cineworld.domain.model.{Listings, Movie, Performance}

import cats.effect.IO

trait ListingsService {
  def getMoviesAndPerformancesFor(cinemaId: String, date: String): IO[Listings]
}
