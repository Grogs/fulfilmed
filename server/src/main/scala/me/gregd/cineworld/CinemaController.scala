package me.gregd.cineworld

import javax.inject.Inject

import autowire.Core.Request
import grizzled.slf4j.Logging
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cineworld.Cineworld
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain.{Cinema, CinemaApi, Movie, Performance}
import me.gregd.cineworld.pages.{Films, Index}
import org.joda.time.LocalDate
import play.api.Environment
import play.api.Mode._
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Action
import play.api.mvc.Results.Ok
import play.mvc.Controller
import upickle.Js
import upickle.Js.Obj
import upickle.default.{Reader, Writer}

import scala.concurrent.ExecutionContext.Implicits.global

class CinemaController @Inject() (env: Environment, dao: Cineworld, cinemaService: CinemaService, implicit val movies: MovieDao, implicit val  tmdb: TheMovieDB) extends Controller with Logging {

  implicit val cinemaWrites = Json.writes[Cinema]
  implicit val performanceWrites = Json.writes[Performance]
  implicit val movieWrites = Json.writes[Movie]

  val scriptPaths  = List(
    "assets/fulfilmed-scala-frontend-jsdeps.js",
    "assets/fulfilmed-scala-frontend-" + (env.mode match {
      case Dev | Test => "fastopt.js"
      case Prod => "fullopt.js"
    })
  )

  object ApiServer extends autowire.Server[Js.Value, Reader, Writer] {
    def read[Result: Reader](p: Js.Value) = upickle.default.readJs[Result](p)
    def write[Result: Writer](r: Result) = upickle.default.writeJs(r)
  }

  def api(pathRaw: String) = Action.async { implicit request =>
    logger.debug(s"API request: $pathRaw")
    val path = pathRaw.split("/")
    val body = request.body.asText.getOrElse("")
    val args = upickle.json.read(body).asInstanceOf[Obj].value.toMap
    ApiServer.route[CinemaApi](cinemaService)(
      Request(path, args)
    ).map( res =>
      Ok(upickle.json.write(res))
    )
  }

  def films() = Action(
    Ok(
      Films(scriptPaths).render
    ).as("text/html")
  )

  def index() = Action(
    Ok(
      Index(scriptPaths).render
    ).as("text/html")
  )

  def returnJson[T:Writes](t: => T) = Action(Ok(Json.toJson(t)))

  def cinemas() = returnJson {
    dao.retrieveCinemas()
  }
//  def cinemas_cinemacity() = returnJson {
//    dao.retrieveCinemaCityCinemas()
//  }

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

}
