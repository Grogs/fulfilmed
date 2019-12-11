package me.gregd.cineworld.integration.vue

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.integration.vue.cinemas.{VueCinema, VueCinemasResp}
import me.gregd.cineworld.integration.vue.listings.VueListingsResp
import me.gregd.cineworld.config.VueConfig
import play.api.http.HeaderNames.X_REQUESTED_WITH
import play.api.libs.ws.WSClient
import scalacache.ScalaCache
import scalacache.memoization._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

import io.circe._
import io.circe.generic.auto._

class VueIntegrationService(ws: WSClient, implicit val cache: ScalaCache[Array[Byte]], config: VueConfig) extends LazyLogging {

  private val base    = config.baseUrl
  private val latLong = "http://maps.apple.com/\\?q=([-0-9.]+),([-0-9.]+)".r

  def retrieveCinemas(): Future[Seq[VueCinema]] = {
    curlCinemas().map(
      resp => parser.decode[VueCinemasResp](resp).toTry.get.venues.flatMap(_.cinemas)
    )
  }

  def retrieveLocation(
      vueCinema: VueCinema
  ): Future[Option[(Double, Double)]] = {
    val name = vueCinema.name.toLowerCase.replace(' ', '-')
    curlLocation(name).map { html =>
      latLong.findFirstMatchIn(html).map { res =>
        val lat  = res.group(1).toDouble
        val long = res.group(2).toDouble
        lat -> long
      }
    }
  }

  def retrieveListings(cinemaId: String): Future[VueListingsResp] = {
    curlListings(cinemaId).map(parser.decode[VueListingsResp](_).toTry.get)
  }

  def curlCinemas(): Future[String] = memoize(7.days) {
    ws.url(s"$base/data/locations/")
      .withHttpHeaders(X_REQUESTED_WITH -> "XMLHttpRequest")
      .get()
      .map(_.body)
  }

  def curlLocation(name: String): Future[String] = memoize(7.days) {
    ws.url(s"$base/cinema/$name/whats-on")
      .get()
      .map(_.body)
  }

  def curlListings(cinemaId: String): Future[String] = memoize(1.day) {
    ws.url(s"$base/data/filmswithshowings/$cinemaId")
      .withHttpHeaders(X_REQUESTED_WITH -> "XMLHttpRequest")
      .get()
      .map(_.body)
  }
}
