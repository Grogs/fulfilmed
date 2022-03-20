package me.gregd.cineworld.wiring

import cats.effect.Async
import com.softwaremill.macwire.{Module, wire}
import me.gregd.cineworld.config._
import me.gregd.cineworld.domain.service._
import me.gregd.cineworld.util._

@Module
class DomainServiceWiring(clock: Clock,
                                       integrationWiring: IntegrationWiring,
                                       repositoryWiring: DomainRepositoryWiring,
                                       chainConfig: ChainConfig,
                                       moviesConfig: MoviesConfig) {

  lazy val cineworldService: CineworldService = wire[CineworldService]

  lazy val vueService: VueService = wire[VueService]

  lazy val movieService: MovieService = wire[MovieService]

  lazy val cinemaService: CinemasService = wire[CinemasService]

  lazy val nearbyCinemasService: NearbyCinemasService = wire[NearbyCinemasService]

  lazy val listingService: CompositeListingService = wire[CompositeListingService]

  lazy val defaultListingsService = wire[DefaultListingsService]

}
