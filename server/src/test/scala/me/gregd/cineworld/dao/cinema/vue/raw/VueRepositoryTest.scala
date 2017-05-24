package me.gregd.cineworld.dao.cinema.vue.raw

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import stub.Stubs.withStubbedVue

class VueRepositoryTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers with JsonMatchers {

  test("curlCinemas") {
    withStubbedVue { vue =>
      val response = vue.curlCinemas().futureValue
      response shouldBe validJson
    }
}
  test("curlListings") {
    withStubbedVue { vue =>
      val response = vue.curlListings("10032").futureValue
      response shouldBe validJson
    }
}
  test("retrieveCinemas") {
    withStubbedVue { vue =>
      val vueCinemas = vue.retrieveCinemas().futureValue
      vueCinemas should not be empty
    }
}
  test("retrieveListings") {
    withStubbedVue { vue =>
      val vueListings = vue.retrieveListings("10032").futureValue
      vueListings.films should not be empty
    }
}
}
