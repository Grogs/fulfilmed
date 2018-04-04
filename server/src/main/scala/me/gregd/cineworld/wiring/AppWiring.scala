package me.gregd.cineworld.wiring

import ch.qos.logback.classic.{Logger, LoggerContext}
import com.softwaremill.macwire.wire
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
import org.slf4j.LoggerFactory
import play.api.libs.ws.WSClient
import me.gregd.cineworld.domain.CinemaApi
import scalacache.ScalaCache

trait AppWiring extends ConfigWiring {

  def wsClient: WSClient
  def cache: ScalaCache[Array[Byte]]
  def clock: Clock

  implicit class asFiniteDuration(d: java.time.Duration) {
    def asScala = scala.concurrent.duration.Duration.fromNanos(d.toNanos)
  }

  lazy val inMemoryLog = new InMemoryLog()

  lazy val inMemoryAppender = new InMemoryLogbackAppender(inMemoryLog)
  inMemoryAppender.setContext(LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext])
  inMemoryAppender.start()

  LoggerFactory.getLogger("ROOT").asInstanceOf[Logger].addAppender(inMemoryAppender)

  lazy val scheduler: Scheduler = monix.execution.Scheduler.global

  lazy val theMovieDB: TheMovieDB = wire[TheMovieDB]

  lazy val ratings: Ratings = wire[Ratings]

  lazy val movieDao: Movies with MovieDao = wire[Movies]

  lazy val cineworldRepository: CineworldRepository = wire[CineworldRepository]
  lazy val vueRepository: VueRepository = wire[VueRepository]

  lazy val postcodeService: PostcodeService = wire[PostcodeService]

  lazy val cineworldDao: CineworldCinemaDao = wire[CineworldCinemaDao]
  lazy val vueDao: VueCinemaDao = wire[VueCinemaDao]

  lazy val cinemaService: CinemaService with CinemaApi = wire[CinemaService]

}
