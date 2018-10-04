package me.gregd.cineworld.ingestion

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.softwaremill.macwire.wire
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.util.{Clock, RealClock}
import me.gregd.cineworld.wiring._
import play.api.Mode
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.duration.Duration.Inf

object IngestionRunner extends LazyLogging {

  private val config = Config.load() match {
    case Left(failures) =>
      System.err.println(failures.toList.mkString("Failed to read config, errors:\n\t", "\n\t", ""))
      throw new IllegalArgumentException("Invalid config")
    case Right(conf) => conf
  }

  private val actorSystem = ActorSystem()
  private val wsClient = AhcWSClient()(ActorMaterializer()(actorSystem))
  private val clock: Clock = RealClock
  private val mode = Mode.Prod
  private val wiring: Wiring = wire[Wiring]
  Await.result(wiring.initialise(), Duration.Inf)

  private val ingestionService = wire[IngestionService]

  def main(args: Array[String]): Unit = {

    val dates = Seq(LocalDate.now, LocalDate.now plusDays 1)

    val res = ingestionService.refresh(dates)

    Await.result(res, Inf)

    wsClient.close()
    actorSystem.terminate()
    System.exit(0)
  }
}
