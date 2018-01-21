import com.softwaremill.macwire._
import me.gregd.cineworld.wiring.AppWiring
import me.gregd.cineworld.{CinemaController, DebugController}
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}
import play.filters.hosts.AllowedHostsComponents
import router.Routes

class AppLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context): Application = new Wiring(context).application
}

class Wiring(context: Context)
    extends BuiltInComponentsFromContext(context)
    with AppWiring
    with AhcWSComponents
    with controllers.AssetsComponents
    with AllowedHostsComponents {

  def httpFilters: Seq[EssentialFilter] = Seq(allowedHostsFilter)

  lazy val cinemaController: CinemaController = wire[CinemaController]
  lazy val debugController: DebugController = wire[DebugController]

  lazy val router: Router = {
    // add the prefix string in local scope for the Routes constructor
    val prefix: String = "/"
    wire[Routes]
  }
}
