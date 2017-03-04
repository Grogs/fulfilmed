package me.gregd.cineworld

import akka.stream.Materializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.OneAppPerTest
import play.api.test.{FakeRequest}
import play.api.test.Helpers.{GET, OK, route, _}


class SmokeTest extends FunSuite with Matchers with OneAppPerTest with ScalaFutures {

  implicit val mat = NoMaterial

  test("Get list of cinemas") {
    val resp = route(app, FakeRequest(GET, "/")).get.futureValue
    val body = resp.body.consumeData.futureValue.utf8String
    resp.header.status shouldBe OK
  }
}
