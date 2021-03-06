package me.gregd.cineworld.web

import com.typesafe.scalalogging.LazyLogging
import fulfilmed.BuildInfo
import me.gregd.cineworld.integration.omdb.OmdbIntegrationService
import me.gregd.cineworld.domain.model.{Cinema, Coordinates, Movie}
import me.gregd.cineworld.domain.service.MovieService
import me.gregd.cineworld.integration.tmdb.TmdbIntegrationService
import me.gregd.cineworld.util.InMemoryLog
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

class DebugController(inMemoryLog: InMemoryLog, cc: ControllerComponents) extends AbstractController(cc) with LazyLogging {

  implicit val movieFormat       = Json.format[Movie]
  implicit val coordinatesFormat = Json.format[Coordinates]
  implicit val cinemaFormat      = Json.format[Cinema]

  def version() = Action {
    Ok(BuildInfo.toJson).as("application/json")
  }

  def warmup() = Action(
    Ok("Warmed up.")
//    Future
//      .sequence(
//        Seq(
//          movies.allMoviesCached(),
//          cinemaApi.getCinemasGrouped()
//        ))
//      .map(_ => Ok("Movies and cinema caches are populated."))
//      .recover{
//        case NonFatal(ex) =>
//          logger.error("Warmup failed", ex)
//          InternalServerError(s"Warmup failed with a ${ex.getClass.getSimpleName}, with message: ${ex.getMessage}")
//      }
  )

  def log() = Secured {
    Action {
      Ok(inMemoryLog.log.map(e => s"${e.logger}: ${e.message}").mkString("<p>Log entries:<br>", "<br>", "</p>")).as("text/html")
    }
  }

  def Secured[A](action: Action[A]): Action[A] = Action.async(action.parser) { request =>
    def hash(string: String) = com.google.common.hash.Hashing.sha256().hashUnencodedChars(string + "fulfilmed").toString.toUpperCase
    def valid(username: String, password: String): Boolean = {
      hash(username) == "58452390BFAAC761FE6DAF729CE7328F859252CC96099B1E1C7A4221DE03A507" &&
      hash(password) == "BB33FB98B7CED702C59194CADB757B3AD781B9A76004D512D308F1F7F4A6AE3B"
    }

    val submittedCredentials: Option[List[String]] = for {
      authHeader <- request.headers.get("Authorization")
      parts      <- authHeader.split(' ').drop(1).headOption
    } yield new String(java.util.Base64.getDecoder.decode(parts.getBytes)).split(':').toList

    submittedCredentials
      .collect {
        case username :: password :: Nil if valid(username, password) =>
      }
      .map(_ => action(request))
      .getOrElse {
        Future.successful(Unauthorized.withHeaders("WWW-Authenticate" -> """Basic realm="Secured Area""""))
      }
  }

}
