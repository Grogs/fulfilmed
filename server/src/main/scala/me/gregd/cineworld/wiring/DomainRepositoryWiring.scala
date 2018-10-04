package me.gregd.cineworld.wiring

import com.softwaremill.macwire.{Module, wire}
import me.gregd.cineworld.domain.repository.{SlickCinemaRepository, SlickListingsRepository}

@Module
class DomainRepositoryWiring(databaseWiring: DatabaseWiring) {
  lazy val cinemaRepository = wire[SlickCinemaRepository]
  lazy val listingsRepository = wire[SlickListingsRepository]
}
