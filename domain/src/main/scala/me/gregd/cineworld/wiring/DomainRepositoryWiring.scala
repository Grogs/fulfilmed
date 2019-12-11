package me.gregd.cineworld.wiring

import cats.effect.Async
import com.softwaremill.macwire.{Module, wire}
import me.gregd.cineworld.domain.repository.{SlickCinemaRepository, SlickListingsRepository}

@Module
class DomainRepositoryWiring[F[_]: Async](databaseWiring: DatabaseWiring) {
  lazy val cinemaRepository   = wire[SlickCinemaRepository[F]]
  lazy val listingsRepository = wire[SlickListingsRepository[F]]
}
