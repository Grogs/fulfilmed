import java.nio.file.Files
import javax.inject.{Named => named}

import com.google.inject.{AbstractModule, Provides => provides}
import com.typesafe.config.ConfigFactory
import me.gregd.cineworld.Cache
import me.gregd.cineworld.config.{ApiKeys, CineworldUrl}
import me.gregd.cineworld.dao.movies.RatingsCache
import me.gregd.cineworld.util.{Clock, FileCache, RealClock}
import monix.execution.Scheduler

import scalacache.ScalaCache
import pureconfig._

class Module extends AbstractModule {

  val prop = {
    val config = ConfigFactory.load
    config.getString _
  }

  @provides@named("themoviedb.api-key") def tmdbApiKey = prop("themoviedb.api-key")

  val scalaCache = Cache(ScalaCache(new FileCache(Files.createTempDirectory("fulfilmed-cache").toString)))

  val ratingsCache = new RatingsCache(collection.mutable.Map())

  override def configure(): Unit = {
    bind(classOf[ApiKeys]).toInstance(loadConfig[ApiKeys]("api-keys").right.get)
    bind(classOf[CineworldUrl]).toInstance(CineworldUrl("http://www.cineworld.co.uk"))
    bind(classOf[Scheduler]).toInstance(monix.execution.Scheduler.global)
    bind(classOf[Clock]).toInstance(RealClock)
    bind(classOf[RatingsCache]).toInstance(ratingsCache)
    bind(classOf[Cache]).toInstance(scalaCache)
  }
}

