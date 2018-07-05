package me.gregd.cineworld.wiring

import com.softwaremill.macwire.{Module, wire}
import me.gregd.cineworld.dao.ratings.OmdbIntegrationService
import me.gregd.cineworld.integration.PostcodeIoIntegrationService
import me.gregd.cineworld.integration.cineworld.CineworldIntegrationService
import me.gregd.cineworld.integration.tmdb.TmdbIntegrationService
import me.gregd.cineworld.integration.vue.VueIntegrationService
import me.gregd.cineworld.util.Clock
import monix.execution.Scheduler
import play.api.libs.ws.WSClient
import scalacache.ScalaCache

@Module
class IntegrationWiring(wsClient: WSClient, cache: ScalaCache[Array[Byte]], clock: Clock, scheduler: Scheduler)(vueConfig: VueConfig,
                                                                                                                cineworldConfig: CineworldConfig,
                                                                                                                omdbConfig: OmdbConfig,
                                                                                                                tmdbConfig: TmdbConfig,
                                                                                                                postcodesIoConfig: PostcodesIoConfig) {

  lazy val tmdbIntegrationService: TmdbIntegrationService = wire[TmdbIntegrationService]

  lazy val omdbIntegrationService: OmdbIntegrationService = wire[OmdbIntegrationService]

  lazy val postcodeIntegrationService: PostcodeIoIntegrationService = wire[PostcodeIoIntegrationService]

  lazy val cineworldIntegrationService: CineworldIntegrationService = wire[CineworldIntegrationService]

  lazy val vueIntegrationService: VueIntegrationService = wire[VueIntegrationService]
}
