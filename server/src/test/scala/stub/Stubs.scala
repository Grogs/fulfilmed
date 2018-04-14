package stub

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.ContentTypes.{`application/json`, `text/html(UTF-8)`}
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.stream.ActorMaterializer
import eu.timepit.refined.api.Refined
import me.gregd.cineworld.config._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.{Codec, Source}
import scala.util.Try
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._
import eu.timepit.refined._
import eu.timepit.refined.string.Url

import scala.io.Codec.UTF8

object Stubs {

  private lazy val server = {
    implicit val system = ActorSystem("http-stubs")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    import akka.http.scaladsl.Http

    val exceptionHandler = ExceptionHandler{
      case e =>
        extractUri{ uri =>
          println(s"Request to stubs failed. Url: $uri. Exception:")
          e.printStackTrace()
          complete(HttpResponse(StatusCodes.InternalServerError, entity = s"request failed to $uri"))
        }
    }

    val notFound: Route = DebuggingDirectives.logRequest(("Not found", Logging.ErrorLevel))(complete("Failed"))

    val route: Route = handleExceptions(exceptionHandler) {
      tmdb.route ~ cineworld.route ~ vue.route ~ omdb.route ~ postcodesio.route ~ notFound
    }

    Await.result(Http().bindAndHandle(route, "localhost", 0), 10.seconds)
  }

  private lazy val serverBase = refineV[Url](s"http://127.0.0.1:${server.localAddress.getPort}").right.get

  object tmdb {

    val route: Route =
      (get & path("3" / "movie" / ("now_playing" | "upcoming")) & parameter('api_key, 'page)) { (_, page) =>
        complete(
          HttpEntity(`application/json`,
            Source.fromResource(s"tmdb/now_playing-$page.json")(UTF8).mkString
          )
        )
      } ~ (get & path("3" / "movie" / LongNumber) & parameter('api_key) & parameter('append_to_response)) { (id, _, _) =>
        complete(
          HttpEntity(`application/json`,
            Try {
              Source.fromResource(s"tmdb/movie-$id.json")(UTF8).mkString
            }.getOrElse{
              """{"imdb_id":"tt7777777", "alternative_titles":{"titles":[]}}"""
            }
          )
        )
      }

    lazy val config = TmdbConfig(serverBase, "", TmdbRateLimit(1.second, 1000))
  }

  object cineworld {

    val route: Route =
      (get & path("uk" / "data-api-service" / "v1" / "quickbook" / "10108" / "cinemas" / "with-event" / "until" / Segment) & parameter('attr, 'lang)) { (_, _, _) =>
        complete(
          HttpEntity(`application/json`,
            Source.fromResource("cineworld/cinemas.json")(UTF8).mkString
          )
        )
      } ~ (get & path("uk" / "data-api-service" / "v1" / "quickbook" / "10108" / "film-events" / "in-cinema" / LongNumber / "at-date" / Segment) & parameter('attr, 'lang)) { (cinemaId, date, _, _) =>
        complete(
          HttpEntity(`application/json`,
            Source.fromResource(s"cineworld/listings-$cinemaId.json")(UTF8).mkString
          )
        )
      }

    lazy val config = CineworldConfig(serverBase)
  }

  object vue {

    val route: Route =
      (get & path("data" / "locations"/)) {
        complete(
          HttpEntity(`application/json`,
            Source.fromResource("vue/locations.json")(UTF8).mkString
          )
        )
      } ~ (get & path("data" / "filmswithshowings" / LongNumber)) { id =>
        complete(
          id match {
            case 10032 => HttpEntity(`application/json`,
              Source.fromResource("vue/filmswithshowings-10032.json")(UTF8).mkString
            )
            case 1010882 =>
              HttpEntity(`application/json`,"""{"Response":"False","Error":"Error getting data."}""")
          }
        )
      } ~(get & path("cinema" / Segment / "whats-on")) { name =>
        complete {
          name match {
            case "bury-the-rock" =>
              HttpEntity(`text/html(UTF-8)`, Source.fromResource("vue/bury-the-rock-whats-on.html")(UTF8).mkString)
            case _ => HttpEntity(`text/html(UTF-8)`, "")
          }
        }
      }

    lazy val config = VueConfig(serverBase)
  }

  object omdb {
    val route = {
      (get & parameter('i, 'apikey)) { (imdbId, _) =>
        complete {
          HttpEntity(`application/json`,
            Source.fromResource(s"omdb/$imdbId.json")(UTF8).mkString
          )
        }
      }
    }

    lazy val config = OmdbConfig(serverBase, "")
  }

  object postcodesio {
    val route = {
      (post & path("postcodes")) {
        complete(
          HttpEntity(`application/json`,
            Source.fromResource("postcodesio/response.json")(UTF8).mkString
          )
        )
      }
    }

    lazy val config = PostcodesIoConfig(serverBase)
  }
}
