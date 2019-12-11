package me.gregd.cineworld.web

import com.typesafe.scalalogging.LazyLogging
import io.circe.{Decoder, HCursor, parser}
import me.gregd.cineworld.domain.service._
import monix.eval.Task
import monix.execution.Scheduler
import play.api.Environment
import play.api.Mode._
import play.api.mvc.{AbstractController, ControllerComponents, Result}
import sloth.ServerFailure.{DeserializerError, HandlerError, PathNotFound}
import sloth._
import chameleon.ext.circe._
import cats.syntax.either._

import scala.concurrent.Future
import scala.io.Source
import io.circe.generic.auto._

class CinemaController(env: Environment, cinemaService: Cinemas[Task], listingsService: ListingsService[Task], nearbyCinemasService: NearbyCinemas[Task], cc: ControllerComponents)
    extends AbstractController(cc)
    with LazyLogging {

  val scriptPaths = List(
    "/assets/fulfilmed-scala-frontend-" + (env.mode match {
      case Dev | Test => "fastopt-bundle.js"
      case Prod       => "opt-bundle.js"
    })
  )

  val router = Router[String, Task]
    .route[Cinemas[Task]](cinemaService)
    .route[Listings[Task]](listingsService)
    .route[NearbyCinemas[Task]](nearbyCinemasService)

  def api(pathRaw: String) = Action.async { implicit request =>
    logger.debug(s"API request: $pathRaw")
    val path = pathRaw.split("/").toList

    val res: Either[Result, Task[Result]] =
      for {
        body <- request.body.asText.toRight(BadRequest("Empty request body"))
        req = Request(path, body)
        eventualJson <- router(req).toEither.leftMap {
          case PathNotFound(_)       => NotFound("Invalid path")
          case HandlerError(ex)      => InternalServerError(ex.getMessage)
          case DeserializerError(ex) => InternalServerError(s"Failed to deserialise request body. Reason: ${ex.getMessage}")
        }
        eventualResult = eventualJson.map(json => Ok(json).as("application/json"))
      } yield eventualResult

    res.fold(
      error => Future.successful(error),
      respTask => respTask.runToFuture(Scheduler.global)
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
