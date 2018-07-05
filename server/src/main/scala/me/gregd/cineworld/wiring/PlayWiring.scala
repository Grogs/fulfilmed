package me.gregd.cineworld.wiring

import com.softwaremill.macwire.wire
import me.gregd.cineworld.util.{Clock, RealClock}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.filters.gzip.GzipFilterComponents
import play.filters.hosts.AllowedHostsComponents
import scalacache.ScalaCache

class PlayWiring(context: Context)
    extends BuiltInComponentsFromContext(context)
    with AhcWSComponents
    with controllers.AssetsComponents
    with AllowedHostsComponents
    with GzipFilterComponents {

  lazy val defaults = new controllers.Default

  val webWiring: WebWiring = {
    val clock = RealClock

    val config = Config.load() match {
      case Left(failures) =>
        System.err.println(failures.toList.mkString("Failed to read config, errors:\n\t", "\n\t", ""))
        throw new IllegalArgumentException("Invalid config")
      case Right(conf) => conf
    }

    val mode = environment.mode

    val cache = new CacheWiring(mode).cache

    val domainRepositoryWiring = new DomainRepositoryWiring(config.database)
    wire[WebWiring]
  }

  def httpFilters: Seq[EssentialFilter] = Seq(allowedHostsFilter, gzipFilter, webWiring.loggingFilter)

  lazy val router: Router = webWiring.routes

}
