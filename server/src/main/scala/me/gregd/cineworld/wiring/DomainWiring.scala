package me.gregd.cineworld.wiring

import com.softwaremill.macwire.wire
import me.gregd.cineworld.domain.movies.{MovieDao, Movies}
import me.gregd.cineworld.domain.{CinemaService, CineworldCinemaDao, VueCinemaDao}
import me.gregd.cineworld.util._

class DomainWiring(clock: Clock, config: Config, integrationWiring: IntegrationWiring) {

  import config.movies
  import integrationWiring.{cineworldService, postcodeService, ratings, tmdbService, vueService}

  lazy val cineworldDao = wire[CineworldCinemaDao]

  lazy val vueDao = wire[VueCinemaDao]

  lazy val movieDao: Movies with MovieDao = wire[Movies]

  lazy val cinemaService: CinemaService = wire[CinemaService]

}
