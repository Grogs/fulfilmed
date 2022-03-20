package me.gregd.cineworld.wiring
import cats.effect.IO
import com.softwaremill.macwire.Module
import slick.jdbc.PostgresProfile.backend.DatabaseDef
import slick.jdbc.PostgresProfile.backend.Database
import me.gregd.cineworld.config._

import scala.concurrent.Future

@Module class DatabaseWiring(databaseConfig: DatabaseConfig) {

  val listingsTableName: ListingsTableName = databaseConfig.listingsTableName

  lazy val db: DatabaseDef = Database.forURL(databaseConfig.url, databaseConfig.username.orNull, databaseConfig.password.orNull)

  def initialise(): IO[Unit] = IO.fromFuture(IO(db.run(DatabaseInitialisation.migrate(databaseConfig.listingsTableName))))
}
