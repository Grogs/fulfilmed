import java.nio.file.Files

import com.google.inject.AbstractModule
import com.typesafe.config.ConfigFactory
import me.gregd.cineworld.Cache
import me.gregd.cineworld.config.values.{CineworldUrl, OmdbKey, TmdbKey, TmdbUrl}
import me.gregd.cineworld.dao.movies.RatingsCache
import me.gregd.cineworld.util.{Clock, FileCache, RealClock}
import monix.execution.Scheduler

import scalacache.ScalaCache

class Module extends AbstractModule {

  val prop = {
    val config = ConfigFactory.load
    config.getString _
  }

  val scalaCache = Cache(ScalaCache(new FileCache(Files.createTempDirectory("fulfilmed-cache").toString)))

  val ratingsCache = new RatingsCache(collection.mutable.Map())

  override def configure(): Unit = {
    bind(classOf[OmdbKey]).toInstance(OmdbKey(prop("api-keys.omdb")))
    bind(classOf[TmdbKey]).toInstance(TmdbKey(prop("api-keys.tmdb")))
    bind(classOf[CineworldUrl]).toInstance(CineworldUrl(prop("base-urls.cineworld")))
    bind(classOf[TmdbUrl]).toInstance(TmdbUrl(prop("base-urls.tmdb")))
    bind(classOf[Scheduler]).toInstance(monix.execution.Scheduler.global)
    bind(classOf[Clock]).toInstance(RealClock)
    bind(classOf[RatingsCache]).toInstance(ratingsCache)
    bind(classOf[Cache]).toInstance(scalaCache)
  }
}

