package me.gregd.cineworld

import me.gregd.cineworld.Config.cineworld
import me.gregd.cineworld.dao.cineworld.{Cineworld, Film}
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.domain.{Movie, Performance, Cinema}
import org.joda.time.LocalDate
import play.api.mvc.Action
import play.api.libs.json.{Writes, Json}
import play.mvc.Controller
import play.api.mvc.Results.Ok

import scala.reflect.ClassTag

class CinemaController(dao: Cineworld) extends Controller {


  implicit val cinemaWrites = Json.writes[Cinema]
  implicit val performanceWrites = Json.writes[Performance]
  implicit val movieWrites = Json.writes[Movie]

  def returnJson[T:Writes](t: => T) = Action(Ok(Json.toJson(t)))

  def cinemas() = {
    dao.retrieveCinemas()
  }
  def cinemas_cinemacity() = returnJson {
    dao.retrieveCinemaCityCinemas()
  }

  def getDate(s: String): LocalDate = s match {
    case "today" => new LocalDate
    case "tomorrow" => new LocalDate() plusDays 1
    case other => new LocalDate(other)
  }

  def getFilms(id: String, date: String) = returnJson {
    dao.retrieveMovies(id, getDate(date))(Movies)
  }


  def getPerformances(id: String, date: String) = returnJson {
    dao.retrievePerformances(id, getDate(date))
  }

  def getRating(title: String) = returnJson {
//    logger.info(s"Retrieving rating for ${params("title")}") //TODO
    Film(
      edi = "Fake ID",
      title = title,
      ""
    ).toMovie
  }


}

object CinemaController extends CinemaController(cineworld)