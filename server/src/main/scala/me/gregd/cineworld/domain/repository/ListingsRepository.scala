package me.gregd.cineworld.domain.repository

import java.time.LocalDate

import me.gregd.cineworld.domain.model.{Cinema, Movie, Performance}

import scala.concurrent.Future

trait ListingsRepository {
  def fetch(cinemaId: String, date: LocalDate): Future[Map[Movie, Seq[Performance]]]
  def persist(cinema: Cinema, date: LocalDate)(listings: Map[Movie, Seq[Performance]]): Future[Unit]
}
