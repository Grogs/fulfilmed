package me.gregd.cineworld.domain.service

import me.gregd.cineworld.domain.model.{Cinema, Coordinates}

import cats.effect.IO

trait NearbyCinemas {
  type Chain = String
  type Grouping = String

  def getNearbyCinemas(coordinates: Coordinates): IO[Seq[Cinema]]
}