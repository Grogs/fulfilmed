package me.gregd.cineworld.rest

import org.scalatra.ScalatraServlet
import me.gregd.cineworld.dao.cineworld.Cineworld
import org.json4s.DefaultFormats
import org.scalatra.json.NativeJsonSupport
import me.gregd.cineworld.dao.imdb.IMDb

/**
 * Author: Greg Dorrell
 * Date: 10/06/2013
 */
class CinemaService(dao: Cineworld) extends ScalatraServlet with NativeJsonSupport {
  protected implicit val jsonFormats = DefaultFormats.withBigDecimal

  before() {
    contentType = formats("json")
  }


  get("/cinemas") {
    dao.getCinemas()
  }

  get("/cinema/:id") {
    dao.getMovies(params("id"))(IMDb)
  }

  get("/movie/:id/performances") {
    dao.getPerformances(
      params("id")
    )
  }

}
