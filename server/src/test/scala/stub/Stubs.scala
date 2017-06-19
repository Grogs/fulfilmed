package stub

import akka.actor.ActorSystem
import me.gregd.cineworld.config.values.{CineworldUrl, OmdbUrl, TmdbUrl, VueUrl}
import play.api.BuiltInComponents
import play.api.http.ContentTypes.JSON
import play.api.mvc.Action
import play.api.mvc.Results.{NotFound, Ok}
import play.api.routing.Router
import play.api.routing.sird._
import play.core.server.{NettyServerComponents, ServerConfig}

import scala.util.Try

object Stubs {

  private val return404: Router.Routes = {
    case other => Action{
      println(s"Returning 404 for ${other.uri}")
      NotFound
    }
  }

  private lazy val server =
    new NettyServerComponents with BuiltInComponents {
      override lazy val serverConfig = ServerConfig(port = Some(0))
      override lazy val actorSystem: ActorSystem = ActorSystem("TmdbStub")
      lazy val router = Router.from(
        tmdb.routes orElse cineworld.routes orElse vue.routes orElse omdb.routes orElse return404
      )
    }.server

  lazy val serverBase = s"http://127.0.0.1:${server.httpPort.get}"

  object tmdb {
    val routes: Router.Routes = {
      case GET(p"/3/movie/now_playing" ? q"api_key=$key" & q"language=$_" & q"page=$page" & q"region=$_") =>
        Action {
          Ok.sendResource(s"tmdb/now_playing-$page.json").as(JSON)
        }
      case GET(p"/3/movie/$id/alternative_titles" ? q"api_key=$_") =>
        Action {
          Try{
            Ok.sendResource(s"tmdb/alternate-titles-$id.json").as(JSON)
          } getOrElse Ok(s"""{"id":$id,"titles":[]}""").as(JSON)
        }
      case GET(p"/3/movie/$tmdbId" ? q"api_key=$_") =>
        Action {
          Ok("""{"imdb_id":"tt7777777"}""").as(JSON)
        }
    }

    lazy val baseUrl = TmdbUrl(serverBase)
  }

  object cineworld {
    val routes: Router.Routes = {
      case GET(p"/getSites" ? q"json=$_" & q"max=$max") =>
        Action {
          Ok.sendResource("cineworld/cinemas.json")
        }
      case GET(p"/pgm-site" ? q"si=$cinemaId" & q"max=$max") =>
        Action {
          Ok.sendResource(s"cineworld/listings-$cinemaId.json")
        }
    }

    lazy val baseUrl = CineworldUrl(serverBase)
  }

  object vue {
    val routes: Router.Routes = {
      case GET(p"/data/locations") => Action {
        Ok.sendResource("vue/locations.json")
      }
      case GET(p"/data/filmswithshowings/10032") => Action {
        Ok.sendResource("vue/filmswithshowings-10032.json")
      }
    }

    lazy val baseUrl = VueUrl(serverBase)
  }

  object omdb {
    val routes: Router.Routes = {
      case GET(p"/" ? q"i=$imdbId" & q"apikey=$_") => Action {
        Ok.sendResource(s"omdb/$imdbId.json")
      }
    }

    lazy val baseUrl = OmdbUrl(serverBase)
  }
}