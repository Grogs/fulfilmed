package me.gregd.cineworld.domain.service

import cats.effect.IO
import me.gregd.cineworld.domain.model.{Cinema, Coordinates}

import scala.concurrent.Future

trait Cinemas {
  def getCinemas: IO[Seq[Cinema]]
}