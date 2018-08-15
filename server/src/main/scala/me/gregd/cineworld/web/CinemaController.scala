package me.gregd.cineworld.web

import autowire.Core.Request
import com.typesafe.scalalogging.LazyLogging
import io.circe.syntax._
import io.circe.generic.auto._
import me.gregd.cineworld.domain.service.{Cinemas, Listings, NearbyCinemas, NearbyCinemasService}
import play.api.Environment
import play.api.Mode._
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

class CinemaController(env: Environment, cinemaService: Cinemas, listingsService: Listings, nearbyCinemasService: NearbyCinemasService, cc: ControllerComponents) extends AbstractController(cc) with LazyLogging {

  val scriptPaths  = List(
    "/assets/fulfilmed-scala-frontend-" + (env.mode match {
      case Dev | Test => "fastopt-bundle.js"
      case Prod => "opt-bundle.js"
    })
  )

  object ApiServer extends autowire.Server[io.circe.Json, io.circe.Decoder, io.circe.Encoder] {
    def read[Result: io.circe.Decoder](p: io.circe.Json) = p.as[Result].toTry.get
    def write[Result: io.circe.Encoder](r: Result) = r.asJson
  }

  val cinemasServiceRouter = ApiServer.route[Cinemas](cinemaService)
  val listingsServiceRouter = ApiServer.route[Listings](listingsService)
  val nearbyCinemasServiceRouter = ApiServer.route[NearbyCinemas](nearbyCinemasService)

  val router = cinemasServiceRouter orElse listingsServiceRouter orElse nearbyCinemasServiceRouter


  def api(pathRaw: String) = Action.async { implicit request =>
    logger.debug(s"API request: $pathRaw")
    val path = pathRaw.split("/")
    val body = request.body.asText.getOrElse("")
    val args = io.circe.parser.parse(body).toTry.get.asObject.get.toMap
    val req = Request(path, args)

    val response = router(req)

    response.map(res =>
      Ok(res.noSpaces)
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