package me.gregd.cineworld

import autowire.Core.Request
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.domain._
import me.gregd.cineworld.pages.Index
import play.api.Environment
import play.api.Mode._
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import upickle.Js
import upickle.Js.Obj
import upickle.default.{Reader, Writer}

import scala.concurrent.ExecutionContext.Implicits.global

class CinemaController(env: Environment, cinemaService: CinemaService, cc: ControllerComponents) extends AbstractController(cc) with LazyLogging {

  implicit val coordinatesFormat = Json.format[Coordinates]
  implicit val performanceFormat = Json.format[Performance]
  implicit val movieFormat = Json.format[Movie]

  val scriptPaths  = List(
    "assets/fulfilmed-scala-frontend-jsdeps.js",
    "assets/fulfilmed-scala-frontend-" + (env.mode match {
      case Dev | Test => "fastopt.js"
      case Prod => "opt.js"
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

  def index() = Action(
    Ok(
      Index(scriptPaths).render
    ).as("text/html")
  )

}