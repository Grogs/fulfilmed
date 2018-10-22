package me.gregd.cineworld.domain.repository
import me.gregd.cineworld.domain.model.Cinema

import scala.concurrent.Future

trait CinemaRepository {

  def fetchAll(): Future[Seq[Cinema]]

  def persist(cinemas: Seq[Cinema]): Future[Unit]
}
