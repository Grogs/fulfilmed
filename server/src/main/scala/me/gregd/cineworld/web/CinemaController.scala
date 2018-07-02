package me.gregd.cineworld.web

import autowire.Core.Request
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.domain.model.{Coordinates, Movie, Performance}
import me.gregd.cineworld.web.service.{CinemaService, ListingsService}
import play.api.Environment
import play.api.Mode._
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import upickle.Js
import upickle.Js.Obj
import upickle.default.{Reader, Writer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

class CinemaController(env: Environment, cinemaService: CinemaService, listingsService: ListingsService, cc: ControllerComponents) extends AbstractController(cc) with LazyLogging {

  implicit val coordinatesFormat = Json.format[Coordinates]
  implicit val performanceFormat = Json.format[Performance]
  implicit val movieFormat = Json.format[Movie]

  val scriptPaths  = List(
    "/assets/fulfilmed-scala-frontend-" + (env.mode match {
      case Dev | Test => "fastopt-bundle.js"
      case Prod => "opt-bundle.js"
    })
  )

  object ApiServer extends autowire.Server[Js.Value, Reader, Writer] {
    def read[Result: Reader](p: Js.Value) = upickle.default.readJs[Result](p)
    def write[Result: Writer](r: Result) = upickle.default.writeJs(r)
  }

  val cinemasServiceRouter = ApiServer.route[CinemaService](cinemaService)
  val listingsServiceRouter = ApiServer.route[ListingsService](listingsService)

  val router = cinemasServiceRouter orElse listingsServiceRouter


  def api(pathRaw: String) = Action.async { implicit request =>
    logger.debug(s"API request: $pathRaw")
    val path = pathRaw.split("/")
    val body = request.body.asText.getOrElse("")
    val args = upickle.json.read(body).asInstanceOf[Obj].value.toMap
    val req = Request(path, args)

    val response = router(req)

    response.map(res =>
      Ok(upickle.json.write(res))
    )
  }

  def stylesheet(name: String) = Action(
    Ok(
      Source.fromResource(s"styles/$name.css").mkString
    ).as("text/css")
  )

  def index(path: String) = Action(
    Ok(
      IndexPage(scriptPaths).render
    ).as("text/html")
  )

}