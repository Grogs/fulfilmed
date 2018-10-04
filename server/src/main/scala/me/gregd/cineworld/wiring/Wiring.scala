package me.gregd.cineworld.wiring

import com.softwaremill.macwire._
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.util.Clock
import play.api.Mode
import play.api.libs.ws.WSClient

import scala.concurrent.Future

@Module class Wiring(config: Config, clock: Clock, wsClient: WSClient, environment: Mode) extends LazyLogging {
  import config.{omdb, tmdb, cineworld, vue, postcodesIo, movies, database, chains}
  implicit val scheduler = monix.execution.Scheduler.global

  val cacheWiring: CacheWiring = wire[CacheWiring]
  val integrationWiring: IntegrationWiring = wire[IntegrationWiring]
  val databaseWiring: DatabaseWiring = wire[DatabaseWiring]
  val domainRepositoryWiring: DomainRepositoryWiring = wire[DomainRepositoryWiring]
  val domainServiceWiring: DomainServiceWiring = wire[DomainServiceWiring]
  val ingestionWiring: IngestionWiring = wire[IngestionWiring]

  def initialise(): Future[Unit] = {
    logger.info("Initialising")
    for {
      _ <- databaseWiring.initialise()
      _ = logger.info("Intiialised")
    } yield ()
  }
}
