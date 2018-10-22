package me.gregd.cineworld.domain.repository

import java.time.LocalDate

import me.gregd.cineworld.domain.model.{Cinema, Movie, Performance}

import scala.concurrent.Future

trait ListingsRepository {
  def fetch(cinemaId: String, date: LocalDate): Future[Seq[(Movie, Seq[Performance])]]
  def persist(cinemaId: String, date: LocalDate)(listings: Seq[(Movie, Seq[Performance])]): Future[Unit]
}
