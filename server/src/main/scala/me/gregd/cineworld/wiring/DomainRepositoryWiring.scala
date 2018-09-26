package me.gregd.cineworld.wiring

import com.softwaremill.macwire.{Module, wire}
import me.gregd.cineworld.domain.repository.{SlickCinemaRepository, SlickListingsRepository}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

@Module
class DomainRepositoryWiring(databaseConfig: DatabaseConfig) {
  import databaseConfig.listingsTableName

  lazy val db: PostgresProfile.backend.DatabaseDef = Database.forURL(databaseConfig.url, databaseConfig.username.orNull, databaseConfig.password.orNull)
  lazy val cinemaRepository = wire[SlickCinemaRepository]
  lazy val listingsRepository = wire[SlickListingsRepository]

  Await.result(DatabaseInitialisation.migrate(db, listingsTableName), Duration.Inf)
}
