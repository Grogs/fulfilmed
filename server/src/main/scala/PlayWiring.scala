import com.softwaremill.macwire._
import me.gregd.cineworld.util.RealClock
import me.gregd.cineworld.wiring.{AppWiring, CacheWiring, TypesafeConfigWiring}
import me.gregd.cineworld.{CinemaController, DebugController}
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, Mode}
import play.filters.gzip.GzipFilterComponents
import play.filters.hosts.AllowedHostsComponents
import router.Routes

class PlayAppLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context): Application = new PlayWiring(context).application
}

class PlayWiring(context: Context)
    extends BuiltInComponentsFromContext(context)
    with AppWiring
    with CacheWiring
    with TypesafeConfigWiring
    with AhcWSComponents
    with controllers.AssetsComponents
    with AllowedHostsComponents
    with GzipFilterComponents {

  val clock = RealClock

  lazy val mode = environment.mode

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
