package me.gregd.cineworld

import akka.stream.ActorMaterializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.OneAppPerTest
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, OK, route, _}

//
//class SmokeTest extends FunSuite with Matchers with OneAppPerTest with ScalaFutures {
//
//  test("Get list of cinemas") {
//    implicit val materializer = ActorMaterializer()(app.actorSystem)
//
//    val resp = route(app, FakeRequest(GET, "/")).get.futureValue
//    val body = resp.body.consumeData.futureValue.utf8String
//
//    resp.header.status shouldBe OK
//    body should include ("<script>me.gregd.cineworld.frontend.Main().main()</script>")
//  }
//}
