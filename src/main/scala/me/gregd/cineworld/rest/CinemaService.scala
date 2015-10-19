package me.gregd.cineworld.rest

import com.typesafe.scalalogging.slf4j.StrictLogging
import org.joda.time.LocalDate
import org.scalatra.ScalatraServlet
import me.gregd.cineworld.dao.cineworld.{Film, Cineworld}
import org.json4s.DefaultFormats
import org.scalatra.json.NativeJsonSupport
import me.gregd.cineworld.dao.movies.Movies

class CinemaService(dao: Cineworld) extends ScalatraServlet with NativeJsonSupport with StrictLogging {
  protected implicit val jsonFormats = DefaultFormats.withBigDecimal

  before() {
    contentType = formats("json")
  }

  error {
    case e => {
      contentType = "text/html"
      status = 500
      s"Unable to process request due to internal server error. Please wait and retry, or contact me at greg@dorrell.me . (Type of error was ${e.getClass.getSimpleName}) "
    }
  }

  get("/cinemas") {
    dao.retrieveCinemas()
  }
  get("/cinemas/cinemacity") {
    dao.retrieveCinemaCityCinemas()
  }

  def getDate(s: String): LocalDate = s match {
    case "today" => new LocalDate
    case "tomorrow" => new LocalDate() plusDays 1
    case other => new LocalDate(other)
  }

  get("/cinema/:id/films/:date") {
    dao.retrieveMovies(params("id"), getDate(params("date")))(Movies)
  }


  get("/cinema/:id/performances/:date") {
    dao.retrievePerformances(params("id"), getDate(params("date")))
  }

  get("/rating/:title") {
    logger.info(s"Retrieving rating for ${params("title")}")
    Film(
      edi = "Fake ID",
      title = params("title"),
      ""
    ).toMovie
  }

}
