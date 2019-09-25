package me.gregd.cineworld.ingestion

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.softwaremill.macwire.wire
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.config.Config
import me.gregd.cineworld.util.{Clock, RealClock}
import me.gregd.cineworld.wiring._
import monix.eval.Task
import play.api.Mode
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.Await
import scala.concurrent.duration._
import monix.execution.Scheduler.global

object Main extends LazyLogging {

  private val config = Config.load() match {
    case Left(failures) =>
      System.err.println(failures.toList.mkString("Failed to read config, errors:\n\t", "\n\t", ""))
      throw new IllegalArgumentException("Invalid config")
    case Right(conf) => conf
  }

  private val wiring: Wiring[Task] = {
    val actorSystem = ActorSystem()
    val wsClient = AhcWSClient()(ActorMaterializer()(actorSystem))
    val clock: Clock = RealClock
    val mode = Mode.Prod
    wire[Wiring[Task]]
  }

  private val ingestionService = wire[IngestionService]

  def main(args: Array[String]): Unit = {

    Await.result(wiring.initialise(), Duration.Inf)
    Thread.sleep(2500)

    logger.info("Startup complete, scheduling job")

    val job = global.scheduleAtFixedRate(1.minute, 8.hours) {
      val dates = Seq(LocalDate.now, LocalDate.now plusDays 1)
      logger.info("Running ingestor")
      ingestionService.refresh(dates).runAsync(global)
    }
  }
}
