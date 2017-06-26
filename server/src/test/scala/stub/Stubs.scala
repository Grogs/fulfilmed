package stub

import akka.actor.ActorSystem
import me.gregd.cineworld.config.values.{CineworldUrl, OmdbUrl, TmdbUrl, VueUrl}
import play.api.http.ContentTypes.JSON
import play.api.http.{DefaultFileMimeTypesProvider, FileMimeTypesConfiguration}
import play.api.mvc.{ActionBuilder, AnyContent, Request}
import play.api.mvc.Results.{NotFound, Ok}
import play.api.routing.Router
import play.api.routing.sird._
import play.api.{BuiltInComponents, NoHttpFiltersComponents}
import play.core.server._

import scala.util.Try

object Stubs {

  private implicit val fileMimeTypes = new DefaultFileMimeTypesProvider(FileMimeTypesConfiguration()).get

  private lazy val server =
    new AkkaHttpServerComponents with BuiltInComponents with NoHttpFiltersComponents {
      implicit lazy val action = defaultActionBuilder
      override lazy val serverConfig = ServerConfig(port = Some(0))
      override lazy val actorSystem: ActorSystem = ActorSystem("stubbed-services")
      val return404: Router.Routes = {
        case other =>
          action {
            println(s"Returning 404 for ${other.uri}")
            NotFound
          }
      }
      lazy val router = Router.from(
        tmdb.routes orElse cineworld.routes orElse vue.routes orElse omdb.routes orElse return404
      )
    }.server

  lazy val serverBase = s"http://127.0.0.1:${server.httpPort.get}"

  object tmdb {
    def routes(implicit action: ActionBuilder[Request, AnyContent]): Router.Routes = {
      case GET(p"/3/movie/now_playing" ? q"api_key=$key" & q"language=$_" & q"page=$page" & q"region=$_") =>
        action {
          Ok.sendResource(s"tmdb/now_playing-$page.json").as(JSON)
        }
      case GET(p"/3/movie/$id/alternative_titles" ? q"api_key=$_") =>
        action {
          Try {
            Ok.sendResource(s"tmdb/alternate-titles-$id.json").as(JSON)
          } getOrElse Ok(s"""{"id":$id,"titles":[]}""").as(JSON)
        }
      case GET(p"/3/movie/$tmdbId" ? q"api_key=$_") =>
        action {
          Ok("""{"imdb_id":"tt7777777"}""").as(JSON)
        }
    }

    lazy val baseUrl = TmdbUrl(serverBase)
  }

  object cineworld {
    def routes(implicit action: ActionBuilder[Request, AnyContent]): Router.Routes = {
      case GET(p"/getSites" ? q"json=$_" & q"max=$max") =>
        action {
          Ok.sendResource("cineworld/cinemas.json")
        }
      case GET(p"/pgm-site" ? q"si=$cinemaId" & q"max=$max") =>
        action {
          Ok.sendResource(s"cineworld/listings-$cinemaId.json")
        }
    }

    lazy val baseUrl = CineworldUrl(serverBase)
  }

  object vue {
    def routes(implicit action: ActionBuilder[Request, AnyContent]): Router.Routes = {
      case GET(p"/data/locations") =>
        action {
          Ok.sendResource("vue/locations.json")
        }
      case GET(p"/data/filmswithshowings/10032") =>
        action {
          Ok.sendResource("vue/filmswithshowings-10032.json")
        }
    }

    lazy val baseUrl = VueUrl(serverBase)
  }

  object omdb {
    def routes(implicit action: ActionBuilder[Request, AnyContent]): Router.Routes = {
      case GET(p"/" ? q"i=$imdbId" & q"apikey=$_") =>
        action {
          Ok.sendResource(s"omdb/$imdbId.json")
        }
    }

    lazy val baseUrl = OmdbUrl(serverBase)
  }

}
