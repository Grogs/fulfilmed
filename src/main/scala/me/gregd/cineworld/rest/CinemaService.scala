package me.gregd.cineworld.rest

import com.typesafe.scalalogging.slf4j.StrictLogging
import org.scalatra.ScalatraServlet
import me.gregd.cineworld.dao.cineworld.{Film, Cineworld}
import org.json4s.DefaultFormats
import org.scalatra.json.NativeJsonSupport
import me.gregd.cineworld.dao.movies.Movies
import org.fusesource.jansi.Ansi._
import org.fusesource.jansi.Ansi.Color._

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
    dao.getCinemas()
  }

  get("/cinema/:id") {
    dao.getMovies(params("id"))(Movies)
  }

  get("/cinema/:id.curl") {
//    val colors = new  {
//      def f(c:Int) = new {
//        def toString = s"\e[3$cm"
//      }
//      val reset = f(9)
//      val black = f(0)
//      val red = f(1)
//      val green = f(2)
//      val yellow = f(3)
//      val blue = f(4)
//      val magenta = f(5)
//      val cyan = f(6)
//      val lGrey = f(7)

//      val reset = f(9)
//    }
    dao.getMovies(params("id"))(Movies).map{ m =>
      ansi.fg(GREEN).a(m.title)
    }.mkString("\n")
  }

  get("/cinema/:id/performances") {
    dao.getPerformances(params("id"))
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
