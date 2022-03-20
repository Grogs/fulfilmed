package me.gregd.cineworld.ingestion

import java.time.LocalDate
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.effect.{IO, IOApp}
import com.softwaremill.macwire.wire
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.config.Config
import me.gregd.cineworld.util.{Clock, RealClock}
import me.gregd.cineworld.wiring._
import org.typelevel.log4cats.slf4j.Slf4jLogger
import play.api.Mode
import play.api.libs.ws.ahc.AhcWSClient

import java.time.ZoneOffset.UTC
import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends IOApp.Simple {
  private val logger = Slf4jLogger.getLogger[IO]

  private val config = Config.load() match {
    case Left(failures) =>
      System.err.println(failures.toList.mkString("Failed to read config, errors:\n\t", "\n\t", ""))
      throw new IllegalArgumentException("Invalid config")
    case Right(conf) => conf
  }

  private val wiring: Wiring = {
    val actorSystem  = ActorSystem()
    val wsClient     = AhcWSClient()(ActorMaterializer()(actorSystem))
    val clock: Clock = RealClock
    val mode         = Mode.Prod
    wire[Wiring]
  }

  private val ingestionService =
    new IngestionService(
      wiring.domainServiceWiring.cinemaService,
      wiring.domainServiceWiring.listingService,
      wiring.domainRepositoryWiring.listingsRepository,
      wiring.domainRepositoryWiring.cinemaRepository,
      wiring.domainServiceWiring.movieService
    )

  def run = {

    val refresh = logger.info("Running ingestor")
      .flatMap(_ =>
        IO.realTimeInstant
          .flatMap { now =>
            val today = LocalDate.ofInstant(now, UTC)
            val tomorrow = today.plusDays(1)
            ingestionService.refresh(List(today, tomorrow))
          }
      )

    wiring.initialise() >>
      logger.info("Startup complete, scheduling job") >>
      IO.sleep(1.minute) >>
      (refresh >> IO.sleep(8.hours)).foreverM
  }
}
