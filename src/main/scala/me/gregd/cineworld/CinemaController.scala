package me.gregd.cineworld

import javax.inject.Inject

import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cineworld.{Cineworld, Film}
import me.gregd.cineworld.dao.movies.{MovieDao}
import me.gregd.cineworld.domain.{Movie, Performance, Cinema}
import me.gregd.cineworld.pages.{Films, Index}
import org.joda.time.LocalDate
import play.api.{Environment, Application}
import play.api.mvc.Action
import play.api.libs.json.{Writes, Json}
import play.mvc.Controller
import play.api.mvc.Results.Ok
import play.api.Mode._


class CinemaController @Inject() (env: Environment, dao: Cineworld, implicit val movies: MovieDao, implicit val  tmdb: TheMovieDB) extends Controller {

  implicit val cinemaWrites = Json.writes[Cinema]
  implicit val performanceWrites = Json.writes[Performance]
  implicit val movieWrites = Json.writes[Movie]

  def scriptPath = "assets/fulfilmed-scala-frontend-" + (env.mode match {
    case Dev => "fastopt.js"
    case Prod => "fullopt.js"
    case Test => throw new IllegalArgumentException("Shouldn't be used in Test mode")
  })

  def films() = Action(
    Ok(
      Films().render
    ).as("text/html")
  )

  def index() = Action(
    Ok(
      Index(scriptPath).render
    ).as("text/html")
  )

  def returnJson[T:Writes](t: => T) = Action(Ok(Json.toJson(t)))

  def cinemas() = returnJson {
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
    dao.retrieveMovies(id, getDate(date))
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
