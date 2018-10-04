package me.gregd.cineworld.integration.cineworld

import java.time.LocalDate

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.integration.cinema.cineworld.{CinemaResp, ListingsBody, RawEvent, RawFilm}
import me.gregd.cineworld.util.Clock
import me.gregd.cineworld.wiring.CineworldConfig
import play.api.libs.json.Json
import play.api.libs.ws._
import scalacache.ScalaCache
import scalacache.memoization._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class CineworldIntegrationService(ws: WSClient, implicit val cache: ScalaCache[Array[Byte]], config: CineworldConfig, clock: Clock) extends LazyLogging {

  implicit val d = Json.format[RawFilm]
  implicit val c = Json.format[RawEvent]
  implicit val b = Json.format[ListingsBody]
  implicit val a = Json.format[CinemaResp]

  private def curlCinemas(): Future[String] = memoize(1.day) {
    val tomorrow = clock.today().plusDays(1).toString
    val url = s"${config.baseUrl}/uk/data-api-service/v1/quickbook/10108/cinemas/with-event/until/$tomorrow?attr=&lang=en_GB"
    for {
      resp <- ws.url(url).get()
      body = resp.body
      _ = if (body.length < 300) logger.warn(s"Response for $url is suspiciously short!")
    } yield body
  }

  private def curl7DayListings(cinema: String, date: LocalDate): Future[String] = memoize(6.hours) {
    val url = s"${config.baseUrl}/uk/data-api-service/v1/quickbook/10108/film-events/in-cinema/$cinema/at-date/$date?attr=&lang=en_GB"
    ws.url(url)
      .get()
      .map(_.body)
  }

  private def parse(string: String) = {
    try {
      Json.parse(string)
    } catch {
      case e: Throwable =>
        logger.error(s"Failed to parse response from Cineworld. Response was: $string", e)
        throw e
    }
  }

  def retrieveCinemas(): Future[Seq[CinemaResp]] = {
    curlCinemas().map { r =>
      val json = parse(r)
      logger.debug(s"Retrieved cinemas response:\n$r")
      val cinemas = json \ "body" \ "cinemas"
      if (cinemas.isEmpty) logger.error(s"No cinemas found. Response was:\n$r")
      cinemas.as[Seq[CinemaResp]]
    }
  }

  def retrieveListings(cinema: String, date: LocalDate): Future[ListingsBody] = {
    curl7DayListings(cinema, date).map { r =>
      val films = parse(r) \ "body"
      films.as[ListingsBody]
    }
  }

}
