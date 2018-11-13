package me.gregd.cineworld.domain.service

import me.gregd.cineworld.domain.model.{Cinema, Coordinates}

import scala.concurrent.Future

trait NearbyCinemas[F[_]] {
  type Chain = String
  type Grouping = String

  def getNearbyCinemas(coordinates: Coordinates): F[Seq[Cinema]]
}