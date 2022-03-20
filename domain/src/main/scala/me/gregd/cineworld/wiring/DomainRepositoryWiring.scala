package me.gregd.cineworld.wiring

import cats.effect.{Async, IO}
import com.softwaremill.macwire.{Module, wire}
import me.gregd.cineworld.domain.repository.{CinemaRepository, ListingsRepository, SlickCinemaRepository, SlickListingsRepository}

@Module
class DomainRepositoryWiring(databaseWiring: DatabaseWiring) {
  lazy val cinemaRepository: CinemaRepository = wire[SlickCinemaRepository]
  lazy val listingsRepository: ListingsRepository = wire[SlickListingsRepository]
}
