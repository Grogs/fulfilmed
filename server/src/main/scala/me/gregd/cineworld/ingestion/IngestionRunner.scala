package me.gregd.cineworld.ingestion

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.softwaremill.macwire.wire
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.util.{Clock, NoOpCache, RealClock}
import me.gregd.cineworld.wiring._
import monix.execution.Scheduler
import play.api.libs.ws.ahc.AhcWSClient
import scalacache.ScalaCache

import scala.concurrent.Await
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

  private val cache: ScalaCache[Array[Byte]] = NoOpCache.cache
  private val clock: Clock = RealClock
  private val scheduler: Scheduler = Scheduler.global

  import config.{omdb, tmdb, cineworld, vue, postcodesIo, movies, database, chains}

  private val integrationWiring: IntegrationWiring = wire[IntegrationWiring]
  private val domainServiceWiring: DomainServiceWiring = wire[DomainServiceWiring]
  private val domainRepositoryWiring: DomainRepositoryWiring = wire[DomainRepositoryWiring]

  private val ingestionService: IngestionService = wire[IngestionService]

  def main(args: Array[String]): Unit = {

    val dates = Seq(LocalDate.now, LocalDate.now plusDays 1)

    val res = ingestionService.refresh(dates)

    Await.result(res, Inf)

    actorSystem.terminate()
    wsClient.close()
  }
}
