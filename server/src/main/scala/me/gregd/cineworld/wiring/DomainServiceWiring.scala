package me.gregd.cineworld.wiring

import com.softwaremill.macwire.{Module, wire}
import me.gregd.cineworld.domain.service._
import me.gregd.cineworld.util._

@Module
class DomainServiceWiring(clock: Clock, integrationWiring: IntegrationWiring, chainConfig: ChainConfig, moviesConfig: MoviesConfig) {

  lazy val cineworldService: CineworldService = wire[CineworldService]

  lazy val vueService: VueService = wire[VueService]

  lazy val movieService: MovieService = wire[MovieService]

  lazy val cinemaService: CompositeCinemaService = wire[CompositeCinemaService]

  lazy val listingService: CompositeListingService = wire[CompositeListingService]

}
