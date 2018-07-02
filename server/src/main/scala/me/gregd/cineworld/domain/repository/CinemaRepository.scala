package me.gregd.cineworld.domain.repository
import me.gregd.cineworld.domain.model.Cinema

import scala.concurrent.Future

trait CinemaRepository {

  def fetchAll(): Future[List[Cinema]]

  def persist(cinemas: List[Cinema]): Future[Unit]
}
