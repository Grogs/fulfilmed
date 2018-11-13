package me.gregd.cineworld.domain.repository
import me.gregd.cineworld.domain.model.Cinema

trait CinemaRepository[F[_]] {

  def fetchAll(): F[Seq[Cinema]]

  def persist(cinemas: Seq[Cinema]): F[Unit]
}
