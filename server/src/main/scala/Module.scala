import java.io.File
import java.nio.file.Files
import java.util.logging.LogManager

import com.google.inject.AbstractModule
import me.gregd.cineworld.config._
import me.gregd.cineworld.domain.CinemaApi
import me.gregd.cineworld.util._
import me.gregd.cineworld.{Cache, CinemaService}
import monix.execution.Scheduler
import play.api.Mode.Dev
import play.api.{Configuration, Environment}
import eu.timepit.refined.pureconfig.refTypeConfigConvert
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.{Logger, LoggerContext}

import scalacache.ScalaCache

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {

  implicit class asFiniteDuration(d: java.time.Duration) {
    def asScala = scala.concurrent.duration.Duration.fromNanos(d.toNanos)
  }

  val home = System.getProperty("user.home")
  val tmp = System.getProperty("java.io.tmpdir")
  private val cacheLocation = environment.mode match {
    case Dev =>
      val res = new File(s"$home/.fulmfilmed-cache")
      res.mkdir()
      res.toPath.toString
    case _ =>
      Files.createTempDirectory("fulfilmed-cache").toString

  }

  val scalaCache = Cache(ScalaCache(new FileCache(cacheLocation)))

  val inMemoryLog = new InMemoryLog()

  val inMemoryAppender = new InMemoryLogbackAppender(inMemoryLog)
  inMemoryAppender.setContext(LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext])
  inMemoryAppender.start()

  LoggerFactory.getLogger("ROOT").asInstanceOf[Logger].addAppender(inMemoryAppender)

  val config = pureconfig.loadConfig[Config] match {
    case Left(failures) =>
      System.err.println(failures.toList.mkString("Failed to read config, errors:\n\t", "\n\t", ""))
      throw new IllegalArgumentException("Invalid config")
    case Right(conf) => conf
  }

  override def configure(): Unit = {
    bind(classOf[InMemoryLog]).toInstance(inMemoryLog)
    bind(classOf[OmdbConfig]).toInstance(config.omdb)
    bind(classOf[TmdbConfig]).toInstance(config.tmdb)
    bind(classOf[CineworldConfig]).toInstance(config.cineworld)
    bind(classOf[VueConfig]).toInstance(config.vue)
    bind(classOf[CinemaApi]).to(classOf[CinemaService])
    bind(classOf[Scheduler]).toInstance(monix.execution.Scheduler.global)
    bind(classOf[Clock]).toInstance(RealClock)
    bind(classOf[Cache]).toInstance(scalaCache)
  }
}
