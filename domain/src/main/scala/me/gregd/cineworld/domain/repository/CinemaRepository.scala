package me.gregd.cineworld.domain.repository
import cats.effect.IO
import me.gregd.cineworld.domain.model.Cinema

trait CinemaRepository {

  def fetchAll(): IO[Seq[Cinema]]

  def persist(cinemas: Seq[Cinema]): IO[Unit]
}
