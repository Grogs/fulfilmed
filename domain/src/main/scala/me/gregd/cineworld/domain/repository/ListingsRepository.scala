package me.gregd.cineworld.domain.repository

import cats.effect.IO

import java.time.LocalDate
import me.gregd.cineworld.domain.model.{Movie, MovieListing, Performance}

trait ListingsRepository {
  def fetch(cinemaId: String, date: LocalDate): IO[Seq[MovieListing]]
  def persist(cinemaId: String, date: LocalDate)(listings: Seq[MovieListing]): IO[Unit]
}
