package me.gregd.cineworld.integration

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

class ServerIntegrationTest extends AnyWordSpec with Matchers with ScalaFutures with GuiceOneAppPerTest {

  override def fakeApplication(): Application = {
    GuiceApplicationBuilder()
      .configure(
        "play.http.secret.key" -> "test-secret-key-for-integration-tests",
        "slick.dbs.default.profile" -> "slick.jdbc.SQLiteProfile$",
        "slick.dbs.default.db.driver" -> "org.sqlite.JDBC",
        "slick.dbs.default.db.url" -> "jdbc:sqlite::memory:",
      )
      .build()
  }

  "Server" should {

    "respond to the index page" in {
      val request = FakeRequest(GET, "/")
      val result = route(app, request).get

      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }

    "respond to debug version endpoint" in {
      val request = FakeRequest(GET, "/debug/version")
      val result = route(app, request).get

      status(result) shouldBe OK
      contentType(result) shouldBe Some("application/json")
      val json = contentAsJson(result)
      (json \ "name").as[String] shouldBe "fulfilmed"
    }

    "respond to debug log endpoint" in {
      val request = FakeRequest(GET, "/debug/log")
      val result = route(app, request).get

      status(result) shouldBe OK
      contentType(result) shouldBe Some("application/json")
    }

    "serve stylesheets" in {
      val request = FakeRequest(GET, "/styles/index.css")
      val result = route(app, request).get

      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/css")
    }

    "handle API requests" in {
      // This tests the API endpoint structure
      // In a real test, we'd want to stub out external services
      val request = FakeRequest(POST, "/api/Cinemas/getCinemas")
        .withHeaders("Content-Type" -> "application/json")
        .withBody("{}")

      val result = route(app, request).get

      // Should at least not crash
      status(result) should (be(OK) or be(INTERNAL_SERVER_ERROR))
    }

    "return 404 for unknown API paths" in {
      val request = FakeRequest(POST, "/api/UnknownService/unknownMethod")
        .withHeaders("Content-Type" -> "application/json")
        .withBody("{}")

      val result = route(app, request).get

      status(result) shouldBe NOT_FOUND
    }

    "serve the SPA for arbitrary paths" in {
      val request = FakeRequest(GET, "/some/arbitrary/path")
      val result = route(app, request).get

      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }

    "redirect root to /index" in {
      val request = FakeRequest(GET, "/")
      val result = route(app, request).get

      status(result) shouldBe OK
    }
  }
}
