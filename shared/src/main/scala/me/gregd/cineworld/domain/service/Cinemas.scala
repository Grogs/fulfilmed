package me.gregd.cineworld.domain.service

import me.gregd.cineworld.domain.model.{Cinema, Coordinates}

import scala.concurrent.Future

trait Cinemas[F[_]] {
  def getCinemas(): F[Seq[Cinema]]
}