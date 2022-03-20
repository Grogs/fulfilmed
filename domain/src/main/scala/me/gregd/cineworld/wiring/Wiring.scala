package me.gregd.cineworld.wiring

import cats.effect.{Async, IO}
import com.softwaremill.macwire._
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.config.Config
import me.gregd.cineworld.util.Clock
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import play.api.Mode
import play.api.libs.ws.WSClient

import scala.concurrent.Future

@Module class Wiring(config: Config, clock: Clock, wsClient: WSClient, environment: Mode) {
  private val logger = Slf4jLogger.getLogger[IO]
  import config.{omdb, tmdb, cineworld, vue, postcodesIo, movies, database, chains}

  val cacheWiring: CacheWiring                       = wire[CacheWiring]
  val integrationWiring: IntegrationWiring           = wire[IntegrationWiring]
  val databaseWiring: DatabaseWiring                 = wire[DatabaseWiring]
  val domainRepositoryWiring: DomainRepositoryWiring = wire[DomainRepositoryWiring]
  val domainServiceWiring: DomainServiceWiring       = wire[DomainServiceWiring]

  def initialise(): IO[Unit] =
    logger.info("Initialising database") >>
      databaseWiring.initialise() >>
      logger.info("Intiialised database")
}
