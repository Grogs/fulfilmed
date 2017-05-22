package me.gregd.cineworld.dao.cinema.vue.raw

import fakes.NoOpCache
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import play.api.mvc.{Action, Results}
import play.api.routing.sird._
import play.api.test.WsTestClient
import play.core.server.Server

class VueRepositoryTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers with JsonMatchers {

  test("curlCinemas") (withMockedVue{ vue =>
    val response = vue.curlCinemas().futureValue
    response shouldBe validJson
  })

  test("curlListings") (withMockedVue{ vue =>
    val response = vue.curlListings("10032").futureValue
    response shouldBe validJson
  })

  test("retrieveCinemas") (withMockedVue{ vue =>
    val vueCinemas = vue.retrieveCinemas().futureValue
    vueCinemas should not be empty
  })

  test("retrieveListings") (withMockedVue{ vue =>
    val vueListings = vue.retrieveListings("10032").futureValue
    vueListings.films should not be empty
  })

  def withMockedVue[T](block: VueRepository => T): T = {
    Server.withRouter() {
      case GET(p"/data/locations") => Action {
        Results.Ok.sendResource("vue/locations.json")
      }
      case GET(p"/data/filmswithshowings/10032") => Action {
        Results.Ok.sendResource("vue/filmswithshowings-10032.json")
      }
    } { implicit port =>
      WsTestClient.withClient { client =>
        block(new VueRepository(client, NoOpCache.cache, ""))
      }
    }
  }

}
