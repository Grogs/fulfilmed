package me.gregd.cineworld.domain.repository

import java.time.LocalDate

import me.gregd.cineworld.domain.model.{Movie, Performance}

trait ListingsRepository[F[_]] {
  def fetch(cinemaId: String, date: LocalDate): F[Seq[(Movie, Seq[Performance])]]
  def persist(cinemaId: String, date: LocalDate)(listings: Seq[(Movie, Seq[Performance])]): F[Unit]
}
