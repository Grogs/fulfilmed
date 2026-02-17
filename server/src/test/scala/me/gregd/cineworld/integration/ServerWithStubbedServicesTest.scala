package me.gregd.cineworld.integration

import cats.effect.IO
import me.gregd.cineworld.domain.model.{Cinema, Coordinates}
import me.gregd.cineworld.domain.service._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._
import play.api.Application

import java.time.LocalDate

/**
 * Integration test that stubs external services to test the full stack
 */
class ServerWithStubbedServicesTest extends AnyWordSpec with Matchers with ScalaFutures with GuiceOneAppPerTest {

  // Stub implementations for testing
  class StubCinemasService extends Cinemas {
    override def getCinemas: IO[Seq[Cinema]] = {
      IO.pure(Seq(
        Cinema("1", "Test Cinema 1", Coordinates(51.5074, -0.1278)),
        Cinema("2", "Test Cinema 2", Coordinates(51.5155, -0.1415))
      ))
    }
  }

  class StubNearbyCinemasService extends NearbyCinemas {
    override def nearest(coordinates: Coordinates, maxDistance: Double, maxCount: Int): IO[Seq[Cinema]] = {
      IO.pure(Seq(
        Cinema("1", "Nearby Cinema 1", Coordinates(51.5074, -0.1278))
      ))
    }
  }

  class StubListingsService extends DefaultListingsService(null, null, null, null) {
    override def getMoviesAndPerformances(cinemaId: String, date: LocalDate) = {
      IO.pure(Seq.empty)
    }
  }

  override def fakeApplication(): Application = {
    GuiceApplicationBuilder()
      .overrides(
        bind[Cinemas].toInstance(new StubCinemasService),
        bind[NearbyCinemas].toInstance(new StubNearbyCinemasService),
        bind[DefaultListingsService].toInstance(new StubListingsService)
      )
      .configure(
        "play.http.secret.key" -> "test-secret-key-for-integration-tests",
        "slick.dbs.default.profile" -> "slick.jdbc.SQLiteProfile$",
        "slick.dbs.default.db.driver" -> "org.sqlite.JDBC",
        "slick.dbs.default.db.url" -> "jdbc:sqlite::memory:",
      )
      .build()
  }

  "Server with stubbed services" should {

    "successfully call getCinemas API with stubbed data" in {
      val request = FakeRequest(POST, "/api/Cinemas/getCinemas")
        .withHeaders("Content-Type" -> "application/json")
        .withBody("{}")

      val result = route(app, request).get

      status(result) shouldBe OK
      contentType(result) shouldBe Some("application/json")

      val content = contentAsString(result)
      content should include("Test Cinema 1")
      content should include("Test Cinema 2")
    }

    "serve pages without crashing" in {
      val request = FakeRequest(GET, "/films")
      val result = route(app, request).get

      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }
  }
}
