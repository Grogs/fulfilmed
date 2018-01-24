package me.gregd.cineworld.dao.cinema.vue.raw

import me.gregd.cineworld.config.VueConfig
import me.gregd.cineworld.dao.cinema.vue.raw.model.cinemas.{VueCinema, VueCinemasResp}
import me.gregd.cineworld.dao.cinema.vue.raw.model.listings.VueListingsResp
import org.json4s.native.JsonMethods._
import org.json4s.{DefaultFormats, _}
import play.api.http.HeaderNames.X_REQUESTED_WITH
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scalacache.ScalaCache
import scalacache.memoization._

class VueRepository(ws: WSClient, implicit val cache: ScalaCache[Array[Byte]], config: VueConfig) {

  private implicit val formats = DefaultFormats

  private val base = config.baseUrl
  private val latLong = "http://maps.apple.com/\\?q=([-0-9.]+),([-0-9.]+)".r

  def retrieveCinemas(): Future[Seq[VueCinema]] = {
    curlCinemas().map(resp =>
      parse(resp).extract[VueCinemasResp].venues.flatMap(_.cinemas)
    )
  }

  def retrieveLocation(vueCinema: VueCinema): Future[Option[(Double, Double)]] = {
    val name = vueCinema.name.toLowerCase.replace(' ', '-')
    curlLocation(name).map { html =>
      latLong.findFirstMatchIn(html).map { res =>
        val lat = res.group(1).toDouble
        val long = res.group(2).toDouble
        lat -> long
      }
    }
  }

  def retrieveListings(cinemaId: String): Future[VueListingsResp] = {
    curlListings(cinemaId).map(resp =>
      parse(resp).extract[VueListingsResp]
    )
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
