package me.gregd.cineworld.domain.service

import me.gregd.cineworld.domain.model.{Cinema, Coordinates}

import scala.concurrent.Future

trait Cinemas {
  def getCinemas(): Future[Seq[Cinema]]
}