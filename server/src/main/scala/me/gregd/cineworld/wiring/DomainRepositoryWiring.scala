package me.gregd.cineworld.wiring

import com.softwaremill.macwire.{Module, wire}
import me.gregd.cineworld.domain.repository.{SlickCinemaRepository, SlickListingsRepository}
import slick.jdbc.SQLiteProfile
import slick.jdbc.SQLiteProfile.api._

@Module
class DomainRepositoryWiring(databaseConfig: DatabaseConfig) {

  lazy val db: SQLiteProfile.backend.DatabaseDef = Database.forURL(databaseConfig.url)
  lazy val cinemaRepository = wire[SlickCinemaRepository]
  lazy val listingsRepository = wire[SlickListingsRepository]

  cinemaRepository.create()
  listingsRepository.create()
}
