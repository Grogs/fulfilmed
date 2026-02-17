package me.gregd.cineworld.integration

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

class ServerIntegrationTest extends AnyFunSuite with Matchers with BaseOneAppPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "db.default.url" -> "jdbc:sqlite::memory:",
        "db.default.driver" -> "org.sqlite.JDBC",
        "chains.enabled" -> Seq.empty[String], // Disable external integrations
        "movies.refreshInterval" -> "1 hour"
      )
      .build()

  test("GET /debug/version should return version information") {
    val request = FakeRequest(GET, "/debug/version")
    val result = route(app, request).get

    status(result) shouldBe OK
    contentType(result) shouldBe Some("application/json")

    val json = contentAsJson(result)
    (json \ "name").asOpt[String] shouldBe defined
    (json \ "version").asOpt[String] shouldBe defined
  }

  test("GET / should redirect to /index") {
    val request = FakeRequest(GET, "/")
    val result = route(app, request).get

    status(result) shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/index")
  }

  test("GET /index should return HTML page") {
    val request = FakeRequest(GET, "/index")
    val result = route(app, request).get

    status(result) shouldBe OK
    contentType(result) shouldBe Some("text/html")
    contentAsString(result) should include("fulfilmed")
  }

  test("GET /debug/warmup should complete successfully") {
    val request = FakeRequest(GET, "/debug/warmup")
    val result = route(app, request).get

    status(result) shouldBe OK
  }
}
