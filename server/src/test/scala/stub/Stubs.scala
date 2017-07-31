package stub

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.stream.ActorMaterializer
import me.gregd.cineworld.config.values.{CineworldUrl, OmdbUrl, TmdbUrl, VueUrl}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.Source
import scala.util.Try

object Stubs {

  private lazy val server = {
    implicit val system = ActorSystem("http-stubs")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    import akka.http.scaladsl.Http

    val notFound: Route = DebuggingDirectives.logRequest(("Not found", Logging.ErrorLevel))(complete("Failed"))

    val route: Route = tmdb.route ~ cineworld.route ~ vue.route ~ omdb.route ~ notFound
    Await.result(Http().bindAndHandle(route, "localhost", 0), 10.seconds)
  }

  private lazy val serverBase = s"http://127.0.0.1:${server.localAddress.getPort}"

  object tmdb {

    val route: Route =
      (get & path("3" / "movie" / "now_playing") & parameter('api_key, 'page)) { (_, page) =>
        complete(
          HttpEntity(`application/json`,
            Source.fromResource(s"tmdb/now_playing-$page.json").mkString
          )
        )
      } ~ (get & path("3" / "movie" / Segment / "alternative_titles")) { id =>
        complete(
          HttpEntity(`application/json`,
            Try {
              Source.fromResource(s"tmdb/alternate-titles-$id.json").mkString
            }.getOrElse(s"""{"id":$id,"titles":[]}""")
          )
        )
      } ~ (get & path("3" / "movie" / LongNumber) & parameter('api_key)) { (_, _) =>
        complete(
          HttpEntity(`application/json`,
            """{"imdb_id":"tt7777777"}"""
          )
        )
      }

    lazy val baseUrl = TmdbUrl(serverBase)
  }

  object cineworld {

    val route: Route =
      (get & path("getSites") & parameter('json, 'max)) { (_, max) =>
        complete(
          HttpEntity(`application/json`,
            Source.fromResource("cineworld/cinemas.json").mkString
          )
        )
      } ~ (get & path("pgm-site") & parameter('si, 'max)) { (cinemaId, _) =>
        complete(
          HttpEntity(`application/json`,
            Source.fromResource(s"cineworld/listings-$cinemaId.json").mkString
          )
        )
      }

    lazy val baseUrl = CineworldUrl(serverBase)
  }

  object vue {

    val route: Route =
      (get & path("data" / "locations"/)) {
        complete(
          HttpEntity(`application/json`,
            Source.fromResource("vue/locations.json").mkString
          )
        )
      } ~ (get & path("data" / "filmswithshowings" / LongNumber)) { id =>
        complete(
          id match {
            case 10032 => HttpEntity(`application/json`,
              Source.fromResource("vue/filmswithshowings-10032.json").mkString
            )
            case 1010882 =>
              HttpEntity(`application/json`,"""{"Response":"False","Error":"Error getting data."}""")
          }
        )
      }

    lazy val baseUrl = VueUrl(serverBase)
  }

  object omdb {
    val route = {
      (get & parameter('i, 'apikey)) { (imdbId, _) =>
        complete {
          HttpEntity(`application/json`,
            Source.fromResource(s"omdb/$imdbId.json").mkString
          )
        }
      }
    }

    lazy val baseUrl = OmdbUrl(serverBase)
  }

}
