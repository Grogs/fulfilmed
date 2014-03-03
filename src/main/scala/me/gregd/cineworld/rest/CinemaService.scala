package me.gregd.cineworld.rest

import org.scalatra.ScalatraServlet
import me.gregd.cineworld.dao.cineworld.{Film, Cineworld}
import org.json4s.DefaultFormats
import org.scalatra.json.NativeJsonSupport
import me.gregd.cineworld.dao.imdb.Ratings
import com.typesafe.scalalogging.slf4j.Logging

class CinemaService(dao: Cineworld) extends ScalatraServlet with NativeJsonSupport with Logging {
  protected implicit val jsonFormats = DefaultFormats.withBigDecimal

  before() {
    contentType = formats("json")
  }


  get("/cinemas") {
    dao.getCinemas()
  }

  get("/cinema/:id") {
    dao.getMovies(params("id"))(Ratings)
  }

  get("/movie/:id/performances") {
    dao.getPerformances(
      params("id")
    )
  }

  get("/rating/:title") {
    logger.info(s"Retrieving rating for ${params("title")}")
    Film(
      edi = "Fake ID",
      title = params("title")
    ).toMovie
  }

}
