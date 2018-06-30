package me.gregd.cineworld.wiring

import com.softwaremill.macwire.wire
import me.gregd.cineworld.dao.ratings.OmdbIntegrationService
import me.gregd.cineworld.integration.PostcodeIoIntegrationService
import me.gregd.cineworld.integration.cineworld.CineworldIntegrationService
import me.gregd.cineworld.integration.tmdb.TmdbIntegrationService
import me.gregd.cineworld.integration.vue.VueIntegrationService
import me.gregd.cineworld.util.Clock
import monix.execution.Scheduler
import play.api.libs.ws.WSClient
import scalacache.ScalaCache

class IntegrationWiring(wsClient: WSClient, cache: ScalaCache[Array[Byte]], clock: Clock, scheduler: Scheduler, config: Config) {

  import config.{vue, cineworld, omdb, tmdb, postcodesIo}

  lazy val tmdbService: TmdbIntegrationService = wire[TmdbIntegrationService]

  lazy val ratings: OmdbIntegrationService = wire[OmdbIntegrationService]

  lazy val postcodeService: PostcodeIoIntegrationService = wire[PostcodeIoIntegrationService]

  lazy val cineworldService: CineworldIntegrationService = wire[CineworldIntegrationService]

  lazy val vueService: VueIntegrationService = wire[VueIntegrationService]
}
