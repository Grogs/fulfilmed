package me.gregd.cineworld.wiring
import com.softwaremill.macwire.Module
import slick.jdbc.PostgresProfile.backend.DatabaseDef
import slick.jdbc.PostgresProfile.backend.Database
import me.gregd.cineworld.config._

import scala.concurrent.Future

@Module class DatabaseWiring(databaseConfig: DatabaseConfig) {

  val listingsTableName: ListingsTableName = databaseConfig.listingsTableName

  lazy val db: DatabaseDef = Database.forURL(databaseConfig.url, databaseConfig.username.orNull, databaseConfig.password.orNull)

  def initialise(): Future[Unit] = DatabaseInitialisation.migrate(db, databaseConfig.listingsTableName)
}
