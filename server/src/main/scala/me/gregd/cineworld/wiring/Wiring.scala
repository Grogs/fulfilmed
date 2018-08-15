package me.gregd.cineworld.wiring

import com.softwaremill.macwire._
import me.gregd.cineworld.util.Clock
import play.api.Mode
import play.api.libs.ws.WSClient

@Module class Wiring(config: Config, clock: Clock, wsClient: WSClient, environment: Mode) {
  import config.{omdb, tmdb, cineworld, vue, postcodesIo, movies, database, chains}
  implicit val scheduler = monix.execution.Scheduler.global

  val cacheWiring: CacheWiring = wire[CacheWiring]
  val integrationWiring: IntegrationWiring = wire[IntegrationWiring]
  val domainRepositoryWiring: DomainRepositoryWiring = wire[DomainRepositoryWiring]
  val domainServiceWiring: DomainServiceWiring = wire[DomainServiceWiring]
  val ingestionWiring: IngestionWiring = wire[IngestionWiring]
}
