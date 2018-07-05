package me.gregd.cineworld.wiring

import akka.stream.Materializer
import ch.qos.logback.classic.{Logger, LoggerContext}
import com.softwaremill.macwire.wire
import controllers.{Assets, Default}
import me.gregd.cineworld.util.{Clock, InMemoryLog, InMemoryLogbackAppender, LoggingFilter}
import me.gregd.cineworld.web.service.{DefaultCinemaService, DefaultListingService}
import me.gregd.cineworld.web.{CinemaController, DebugController}
import org.slf4j.LoggerFactory
import play.api.Environment
import play.api.http.HttpErrorHandler
import play.api.mvc.ControllerComponents
import router.Routes

import scala.concurrent.ExecutionContext

class WebWiring(domainRepositoryWiring: DomainRepositoryWiring)(controllerComponents: ControllerComponents,
                                                                errorHandler: HttpErrorHandler,
                                                                assets: Assets,
                                                                environment: Environment,
                                                                default: Default,
                                                                clock: Clock,
                                                                implicit val mat: Materializer,
                                                                implicit val ec: ExecutionContext) {

  private val prefix: String = "/"

  import domainRepositoryWiring._

  val inMemoryLog: InMemoryLog = {
    val res = new InMemoryLog()

    val inMemoryAppender: InMemoryLogbackAppender = new InMemoryLogbackAppender(inMemoryLog)
    inMemoryAppender.setContext(LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext])
    inMemoryAppender.start()

    LoggerFactory.getLogger("ROOT").asInstanceOf[Logger].addAppender(inMemoryAppender)

    res
  }

  private val defaultCinemaService = wire[DefaultCinemaService]
  private val defaultListingsService = wire[DefaultListingService]

  private val debugController = wire[DebugController]
  private val cinemaController = wire[CinemaController]

  lazy val loggingFilter = wire[LoggingFilter]

  lazy val routes = wire[Routes]

}
