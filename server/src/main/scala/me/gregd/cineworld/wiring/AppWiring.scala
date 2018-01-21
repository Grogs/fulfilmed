package me.gregd.cineworld.wiring

import java.io.File
import java.nio.file.Files

import ch.qos.logback.classic.{Logger, LoggerContext}
import com.softwaremill.macwire.{Module, wire}
import me.gregd.cineworld.config.Config
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cinema.cineworld.CineworldCinemaDao
import me.gregd.cineworld.dao.cinema.cineworld.raw.CineworldRepository
import me.gregd.cineworld.dao.cinema.vue.VueCinemaDao
import me.gregd.cineworld.dao.cinema.vue.raw.VueRepository
import me.gregd.cineworld.dao.movies.{MovieDao, Movies}
import me.gregd.cineworld.dao.ratings.Ratings
import me.gregd.cineworld.util._
import me.gregd.cineworld.{Cache, CinemaService}
import monix.execution.Scheduler
import org.slf4j.LoggerFactory
import play.api.Environment
import play.api.Mode.Dev
import play.api.libs.ws.WSClient
import eu.timepit.refined.pureconfig.refTypeConfigConvert
import me.gregd.cineworld.domain.CinemaApi

import scalacache.ScalaCache

trait AppWiring {

  def environment: Environment
  def wsClient: WSClient

  implicit class asFiniteDuration(d: java.time.Duration) {
    def asScala = scala.concurrent.duration.Duration.fromNanos(d.toNanos)
  }

  lazy val inMemoryLog = new InMemoryLog()

  lazy val inMemoryAppender = new InMemoryLogbackAppender(inMemoryLog)
  inMemoryAppender.setContext(LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext])
  inMemoryAppender.start()

  LoggerFactory.getLogger("ROOT").asInstanceOf[Logger].addAppender(inMemoryAppender)

  lazy val appConfig = pureconfig.loadConfig[Config] match {
    case Left(failures) =>
      System.err.println(failures.toList.mkString("Failed to read config, errors:\n\t", "\n\t", ""))
      throw new IllegalArgumentException("Invalid config")
    case Right(conf) => conf
  }

  lazy val omdbConfig = appConfig.omdb
  lazy val tmdbConfig = appConfig.tmdb
  lazy val cineworldConfig = appConfig.cineworld
  lazy val vueConfig = appConfig.vue

  lazy val cache: Cache = {
    val home = System.getProperty("user.home")
    val tmp = System.getProperty("java.io.tmpdir")
    val cacheLocation = environment.mode match {
      case Dev =>
        val res = new File(s"$home/.fulmfilmed-cache")
        res.mkdir()
        res.toPath.toString
      case _ =>
        Files.createTempDirectory("fulfilmed-cache").toString

    }

    Cache(ScalaCache(new FileCache(cacheLocation)))
  }

  lazy val _clock: Clock = RealClock

  lazy val scheduler: Scheduler = monix.execution.Scheduler.global

  lazy val theMovieDB: TheMovieDB = wire[TheMovieDB]

  lazy val ratings: Ratings = wire[Ratings]

  lazy val movieDao: Movies with MovieDao = wire[Movies]

  lazy val cineworldRepository: CineworldRepository = wire[CineworldRepository]
  lazy val vueRepository: VueRepository = wire[VueRepository]

  lazy val cineworldDao: CineworldCinemaDao = wire[CineworldCinemaDao]
  lazy val vueDao: VueCinemaDao = wire[VueCinemaDao]

  lazy val cinemaService: CinemaService with CinemaApi = wire[CinemaService]

}
