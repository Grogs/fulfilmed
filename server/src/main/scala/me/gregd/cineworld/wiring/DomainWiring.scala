package me.gregd.cineworld.wiring

import com.softwaremill.macwire.wire
import me.gregd.cineworld.domain.{CinemasService, ListingsService}
import me.gregd.cineworld.domain.service._
import me.gregd.cineworld.util._

class DomainWiring(clock: Clock, config: Config, integrationWiring: IntegrationWiring) {

  import config.movies
  import integrationWiring.{cineworldService, postcodeService, ratings, tmdbService, vueService}

  lazy val cineworldDao = wire[CineworldService]

  lazy val vueDao = wire[VueService]

  lazy val movieDao: MovieService = wire[MovieService]

  lazy val cinemaService: CinemasService = wire[DefaultCinemasService]

  lazy val listingService: ListingsService = wire[DefaultCinemaListingsService]

}
