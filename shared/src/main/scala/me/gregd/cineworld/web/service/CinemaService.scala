package me.gregd.cineworld.web.service

import me.gregd.cineworld.domain.model.{Cinema, Coordinates}

import scala.concurrent.Future

trait CinemaService {

  type Chain = String
  type Grouping = String

  def getCinemasGrouped(): Future[Map[Chain, Map[Grouping, Seq[Cinema]]]]
  def getNearbyCinemas(coordinates: Coordinates): Future[Seq[Cinema]]
}