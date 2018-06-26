package me.gregd.cineworld.wiring

import com.softwaremill.macwire.wire
import me.gregd.cineworld.config.Config
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cinema.cineworld.CineworldCinemaDao
import me.gregd.cineworld.dao.cinema.cineworld.raw.CineworldRepository
import me.gregd.cineworld.dao.cinema.vue.VueCinemaDao
import me.gregd.cineworld.dao.cinema.vue.raw.VueRepository
import me.gregd.cineworld.dao.movies.{MovieDao, Movies}
import me.gregd.cineworld.dao.ratings.Ratings
import me.gregd.cineworld.util._
import me.gregd.cineworld.{CinemaService, PostcodeService}
import monix.execution.Scheduler
import play.api.libs.ws.WSClient
import scalacache.ScalaCache

class AppWiring(wsClient: WSClient, cache: ScalaCache[Array[Byte]], clock: Clock, config: Config) {

  val omdb = config.omdb
  val tmdb = config.tmdb
  val cineworld = config.cineworld
  val vue = config.vue
  val postcodesIo = config.postcodesIo
  val movies = config.movies

  lazy val scheduler: Scheduler = monix.execution.Scheduler.global

  lazy val postcodeService: PostcodeService = wire[PostcodeService]
  lazy val theMovieDB: TheMovieDB = wire[TheMovieDB]
  lazy val ratings: Ratings = wire[Ratings]

  lazy val movieDao: Movies with MovieDao = wire[Movies]

  lazy val cineworldRepository: CineworldRepository = wire[CineworldRepository]
  lazy val vueRepository: VueRepository = wire[VueRepository]

  lazy val cineworldDao: CineworldCinemaDao = wire[CineworldCinemaDao]
  lazy val vueDao: VueCinemaDao = wire[VueCinemaDao]

  lazy val cinemaService: CinemaService = wire[CinemaService]

}
