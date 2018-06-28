import com.softwaremill.macwire._
import me.gregd.cineworld.util._
import me.gregd.cineworld.wiring._
import monix.execution.Scheduler
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}
import play.filters.gzip.GzipFilterComponents
import play.filters.hosts.AllowedHostsComponents
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

  val integrationWiring = new IntegrationWiring(wsClient, cache, clock, Scheduler.global, config)
  val domainWiring: DomainWiring = new DomainWiring(clock, config, integrationWiring)
  val webWiring: WebWiring = wire[WebWiring]


  lazy val defaults = new controllers.Default


  def httpFilters: Seq[EssentialFilter] = Seq(allowedHostsFilter, gzipFilter, webWiring.loggingFilter)

  lazy val router = webWiring.routes
}
