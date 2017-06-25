package me.gregd.cineworld.dao.cinema.vue.raw

import javax.inject.Inject

import me.gregd.cineworld.Cache
import me.gregd.cineworld.config.values.VueUrl
import me.gregd.cineworld.dao.cinema.vue.raw.model.cinemas.{VueCinema, VueCinemasResp}
import me.gregd.cineworld.dao.cinema.vue.raw.model.listings.VueListingsResp
import org.json4s.{DefaultFormats, _}
import org.json4s.native.JsonMethods._
import play.api.http.HeaderNames.X_REQUESTED_WITH
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scalacache.memoization._

class VueRepository @Inject()(ws: WSClient, cache: Cache, baseUrl: VueUrl) {

  private implicit val _ = cache.scalaCache
  private implicit val formats = DefaultFormats

  private val base = baseUrl.value

  def retrieveCinemas(): Future[Seq[VueCinema]] = {
    curlCinemas().map(resp =>
      parse(resp).extract[VueCinemasResp].venues.flatMap(_.cinemas)
    )
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

  def curlListings(cinemaId: String): Future[String] = memoize(1.day) {
    ws.url(s"$base/data/filmswithshowings/$cinemaId")
      .withHttpHeaders(X_REQUESTED_WITH -> "XMLHttpRequest")
      .get()
      .map(_.body)
  }
}
