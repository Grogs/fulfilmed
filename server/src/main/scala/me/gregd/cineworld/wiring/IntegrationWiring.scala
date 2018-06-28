package me.gregd.cineworld.wiring

import com.softwaremill.macwire.wire
import me.gregd.cineworld.dao.ratings.OmdbService
import me.gregd.cineworld.integration.PostcodeService
import me.gregd.cineworld.integration.cineworld.CineworldService
import me.gregd.cineworld.integration.tmdb.TmdbService
import me.gregd.cineworld.integration.vue.VueService
import me.gregd.cineworld.util.Clock
import monix.execution.Scheduler
import play.api.libs.ws.WSClient
import scalacache.ScalaCache

class IntegrationWiring(wsClient: WSClient, cache: ScalaCache[Array[Byte]], clock: Clock, scheduler: Scheduler, config: Config) {

  import config.{vue, cineworld, omdb, tmdb, postcodesIo}

  lazy val tmdbService: TmdbService = wire[TmdbService]

  lazy val ratings: OmdbService = wire[OmdbService]

  lazy val postcodeService: PostcodeService = wire[PostcodeService]

  lazy val cineworldService: CineworldService = wire[CineworldService]

  lazy val vueService: VueService = wire[VueService]
}
