import java.io.File
import java.nio.file.{Files, Path, Paths}
import javax.inject.Inject

import com.google.inject.AbstractModule
import com.typesafe.config.ConfigFactory
import fulfilmed.BuildInfo
import me.gregd.cineworld.Cache
import me.gregd.cineworld.config.values._
import me.gregd.cineworld.dao.movies.RatingsCache
import me.gregd.cineworld.util.{Clock, FileCache, RealClock}
import monix.execution.Scheduler
import play.api.Mode.Dev
import play.api.{Configuration, Environment}

import scalacache.ScalaCache

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {

  val config = ConfigFactory.load
  val prop = {
    config.getString _
  }

  implicit class asFiniteDuration(d: java.time.Duration) {
    def asScala = scala.concurrent.duration.Duration.fromNanos(d.toNanos)
  }

  val tmdbRateLimit = TmdbRateLimit(config.getDuration("rate-limit.tmdb.duration").asScala, config.getInt("rate-limit.tmdb.count"))

  val home = System.getProperty("user.home")
  val tmp = System.getProperty("java.io.tmpdir")
  private val cacheLocation = environment.mode match {
    case Dev =>
      new File(s"$home/.fulmfilmed-cache")
    case _ =>
      val cacheDir = BuildInfo.gitHeadCommit.getOrElse(BuildInfo.builtAtMillis.toString)
      new File(s"$tmp/fulfilmed-cache/$cacheDir")

  }

  cacheLocation.mkdir()

  val scalaCache = Cache(ScalaCache(new FileCache(cacheLocation.toPath.toString)))

  val ratingsCache = new RatingsCache(collection.mutable.Map())
  override def configure(): Unit = {
    bind(classOf[OmdbKey]).toInstance(OmdbKey(prop("api-keys.omdb")))
    bind(classOf[TmdbKey]).toInstance(TmdbKey(prop("api-keys.tmdb")))
    bind(classOf[CineworldUrl]).toInstance(CineworldUrl(prop("base-urls.cineworld")))
    bind(classOf[VueUrl]).toInstance(VueUrl(prop("base-urls.vue")))
    bind(classOf[TmdbUrl]).toInstance(TmdbUrl(prop("base-urls.tmdb")))
    bind(classOf[OmdbUrl]).toInstance(OmdbUrl(prop("base-urls.omdb")))
    bind(classOf[TmdbRateLimit]).toInstance(tmdbRateLimit)
    bind(classOf[Scheduler]).toInstance(monix.execution.Scheduler.global)
    bind(classOf[Clock]).toInstance(RealClock)
    bind(classOf[RatingsCache]).toInstance(ratingsCache)
    bind(classOf[Cache]).toInstance(scalaCache)
  }
}
