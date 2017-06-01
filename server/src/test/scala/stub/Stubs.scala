package stub

import fakes.NoOpCache
import me.gregd.cineworld.config.values.{CineworldUrl, TmdbKey}
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cinema.cineworld.raw.CineworldRepository
import me.gregd.cineworld.dao.cinema.vue.raw.VueRepository
import play.api.mvc.Action
import play.api.mvc.Results.Ok
import play.api.routing.sird._
import play.api.test.WsTestClient
import play.core.server.Server

object Stubs {
  def withStubbedCineworld[T](block: CineworldRepository => T): T = {
    Server.withRouter() {
      case GET(p"/getSites" ? q"json=$_" & q"max=$max") =>
        Action {
          Ok.sendResource("cineworld/cinemas.json")
        }
      case GET(p"/pgm-site" ? q"si=$cinemaId" & q"max=$max") =>
        Action {
          Ok.sendResource(s"cineworld/listings-$cinemaId.json")
        }
    } { implicit port =>
      WsTestClient.withClient { client =>
        block(new CineworldRepository(client, NoOpCache.cache, CineworldUrl("")))
      }
    }
  }

  def withStubbedVue[T](block: VueRepository => T): T = {
    Server.withRouter() {
      case GET(p"/data/locations") => Action {
        Ok.sendResource("vue/locations.json")
      }
      case GET(p"/data/filmswithshowings/10032") => Action {
        Ok.sendResource("vue/filmswithshowings-10032.json")
      }
    } { implicit port =>
      WsTestClient.withClient { client =>
        block(new VueRepository(client, NoOpCache.cache, ""))
      }
    }
  }

  def withStubbedTMDB[T](block: TheMovieDB => T): T = {
    Server.withRouter() {
      case GET(p"/data/locations") => Action {
        Ok.sendResource("vue/locations.json")
      }
      case GET(p"/data/filmswithshowings/10032") => Action {
        Ok.sendResource("vue/filmswithshowings-10032.json")
      }
    } { implicit port =>
      WsTestClient.withClient { client =>
        block(new TheMovieDB(TmdbKey(""), client))
      }
    }
  }
}
