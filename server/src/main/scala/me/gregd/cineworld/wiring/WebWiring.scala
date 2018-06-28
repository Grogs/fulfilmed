package me.gregd.cineworld.wiring

import akka.stream.Materializer
import ch.qos.logback.classic.{Logger, LoggerContext}
import com.softwaremill.macwire.wire
import controllers.{Assets, Default}
import me.gregd.cineworld.util.{InMemoryLog, InMemoryLogbackAppender, LoggingFilter}
import me.gregd.cineworld.web.{CinemaController, DebugController}
import org.slf4j.LoggerFactory
import play.api.Environment
import play.api.http.HttpErrorHandler
import play.api.mvc.ControllerComponents
import router.Routes

import scala.concurrent.ExecutionContext

class WebWiring(integrationWiring: IntegrationWiring, domainWiring: DomainWiring)(controllerComponents: ControllerComponents,
                                                                                  errorHandler: HttpErrorHandler,
                                                                                  assets: Assets,
                                                                                  environment: Environment,
                                                                                  default: Default,
                                                                                  implicit val mat: Materializer,
                                                                                  implicit val ec: ExecutionContext) {

  private val prefix: String = "/"

  val inMemoryLog: InMemoryLog = {
    val res = new InMemoryLog()

    val inMemoryAppender: InMemoryLogbackAppender = new InMemoryLogbackAppender(inMemoryLog)
    inMemoryAppender.setContext(LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext])
    inMemoryAppender.start()

    LoggerFactory.getLogger("ROOT").asInstanceOf[Logger].addAppender(inMemoryAppender)

    res
  }

  import integrationWiring._, domainWiring._

  private val debugController = wire[DebugController]
  private val cinemaController = wire[CinemaController]

  lazy val loggingFilter = wire[LoggingFilter]

  lazy val routes = wire[Routes]

}
