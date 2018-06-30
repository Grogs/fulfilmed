package me.gregd.cineworld.domain

import scala.concurrent.Future

trait CinemasService {

  type Chain = String
  type Grouping = String

  def getCinemasGrouped(): Future[Map[Chain, Map[Grouping, Seq[Cinema]]]]
  def getCinemas(): Future[Seq[Cinema]]

  def getNearbyCinemas(coordinates: Coordinates): Future[Seq[Cinema]]

}