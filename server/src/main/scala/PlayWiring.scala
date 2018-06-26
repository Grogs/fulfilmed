import ch.qos.logback.classic.{Logger, LoggerContext}
import com.softwaremill.macwire._
import me.gregd.cineworld.config.Config
import me.gregd.cineworld.util.{Clock, InMemoryLog, InMemoryLogbackAppender, RealClock}
import me.gregd.cineworld.wiring.{AppWiring, CacheWiring}
import me.gregd.cineworld.{CinemaController, DebugController}
import org.slf4j.LoggerFactory
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, Mode}
import play.filters.gzip.GzipFilterComponents
import play.filters.hosts.AllowedHostsComponents
import router.Routes
import scalacache.ScalaCache

class PlayAppLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context): Application = new PlayWiring(context).application
}

class PlayWiring(context: Context)
    extends BuiltInComponentsFromContext(context)
    with AhcWSComponents
    with controllers.AssetsComponents
    with AllowedHostsComponents
    with GzipFilterComponents {


  val clock: Clock = RealClock

  val config: Config = Config.load() match {
    case Left(failures) =>
      System.err.println(failures.toList.mkString("Failed to read config, errors:\n\t", "\n\t", ""))
      throw new IllegalArgumentException("Invalid config")
    case Right(conf) => conf
  }

  val mode = environment.mode

  val cache: ScalaCache[Array[Byte]] = new CacheWiring(mode).cache

  val appWiring: AppWiring = new AppWiring(wsClient, cache, clock, config)

  val inMemoryLog = new InMemoryLog()

  val inMemoryAppender = new InMemoryLogbackAppender(inMemoryLog)
  inMemoryAppender.setContext(LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext])
  inMemoryAppender.start()

  LoggerFactory.getLogger("ROOT").asInstanceOf[Logger].addAppender(inMemoryAppender)

  import appWiring._

  lazy val defaults = new controllers.Default

  lazy val loggingFilter = wire[LoggingFilter]

  def httpFilters: Seq[EssentialFilter] = Seq(allowedHostsFilter, gzipFilter, loggingFilter)



  lazy val cinemaController: CinemaController = wire[CinemaController]
  lazy val debugController: DebugController = wire[DebugController]

  lazy val router: Router = {
    // add the prefix string in local scope for the Routes constructor
    val prefix: String = "/"
    wire[Routes]
  }
}
