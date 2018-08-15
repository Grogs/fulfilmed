package me.gregd.cineworld.wiring

import com.softwaremill.macwire.wire
import me.gregd.cineworld.util._
import me.gregd.cineworld.web.{CinemaController, DebugController}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.filters.gzip.GzipFilterComponents
import play.filters.hosts.AllowedHostsComponents
import router.Routes

class PlayWiring(context: Context)
    extends BuiltInComponentsFromContext(context)
    with AhcWSComponents
    with controllers.AssetsComponents
    with AllowedHostsComponents
    with GzipFilterComponents {

  lazy val defaults = new controllers.Default
  lazy val loggingFilter = wire[LoggingFilter]

  lazy val routes = {
    val clock = RealClock

    val config = Config.load() match {
      case Left(failures) =>
        System.err.println(failures.toList.mkString("Failed to read config, errors:\n\t", "\n\t", ""))
        throw new IllegalArgumentException("Invalid config")
      case Right(conf) => conf
    }

    val mode = environment.mode

    val cache = new CacheWiring(mode).cache

    val wiring = wire[Wiring]

    val inMemoryLog = InMemoryLog

    val debugController = wire[DebugController]
    val cinemaController = wire[CinemaController]

    wire[Routes]
  }

  def httpFilters: Seq[EssentialFilter] = Seq(allowedHostsFilter, gzipFilter, loggingFilter)

  lazy val router: Router = routes

}
