package me.gregd.cineworld.integration.vue

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.integration.vue.cinemas.{VueCinema, VueCinemasResp}
import me.gregd.cineworld.integration.vue.listings.VueListingsResp
import me.gregd.cineworld.config.VueConfig
import play.api.http.HeaderNames.X_REQUESTED_WITH
import play.api.libs.ws.WSClient
import scalacache.Cache
import scalacache.memoization._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import io.circe._
import io.circe.generic.auto._

class VueIntegrationService(ws: WSClient, implicit val cache: Cache[IO, String, String], config: VueConfig) extends LazyLogging {

  private val base    = config.baseUrl
  private val latLong = "http://maps.apple.com/\\?q=([-0-9.]+),([-0-9.]+)".r

  def retrieveCinemas(): IO[List[VueCinema]] = {
    curlCinemas().map(
      resp => parser.decode[VueCinemasResp](resp).toTry.get.venues.flatMap(_.cinemas)
    )
  }

  def retrieveLocation(
      vueCinema: VueCinema
  ): IO[Option[(Double, Double)]] = {
    val name = vueCinema.name.toLowerCase.replace(' ', '-')
    curlLocation(name).map { html =>
      latLong.findFirstMatchIn(html).map { res =>
        val lat  = res.group(1).toDouble
        val long = res.group(2).toDouble
        lat -> long
      }
    }
  }

  def retrieveListings(cinemaId: String): IO[VueListingsResp] = {
    curlListings(cinemaId).map(parser.decode[VueListingsResp](_).toTry.get)
  }

  def curlCinemas(): IO[String] = memoizeF(Some(7.days)) {
    IO.fromFuture(
      IO(
        ws.url(s"$base/data/locations/")
          .withHttpHeaders(X_REQUESTED_WITH -> "XMLHttpRequest")
          .get()
          .map(_.body)))
  }

  def curlLocation(name: String): IO[String] = memoizeF(Some(7.days)) {
    IO.fromFuture(
      IO(
        ws.url(s"$base/cinema/$name/whats-on")
          .get()
          .map(_.body)))
  }

  def curlListings(cinemaId: String): IO[String] = memoizeF(Some(1.day)) {
    IO.fromFuture(IO(ws.url(s"$base/data/filmswithshowings/$cinemaId")
      .withHttpHeaders(X_REQUESTED_WITH -> "XMLHttpRequest")
      .get()
      .map(_.body)))
  }
}
