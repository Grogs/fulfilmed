package me.gregd.cineworld.web

import cats.effect.IO
import me.gregd.cineworld.domain.model.{Cinema, Coordinates}
import me.gregd.cineworld.domain.service._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Environment
import play.api.test.Helpers._
import play.api.test.FakeRequest

import java.time.LocalDate

class CinemaControllerTest extends AnyWordSpec with Matchers {

  val testCinemas = Seq(
    Cinema("1", "Test Cinema", Coordinates(51.5074, -0.1278))
  )

  val stubCinemaService = new Cinemas {
    override def getCinemas: IO[Seq[Cinema]] = IO.pure(testCinemas)
  }

  val stubListingsService = new DefaultListingsService(null, null, null, null) {
    override def getMoviesAndPerformances(cinemaId: String, date: LocalDate) = {
      IO.pure(Seq.empty)
    }
  }

  val stubNearbyCinemasService = new NearbyCinemas {
    override def nearest(coordinates: Coordinates, maxDistance: Double, maxCount: Int): IO[Seq[Cinema]] = {
      IO.pure(testCinemas)
    }
  }

  "CinemaController" should {

    "generate script paths for development mode" in {
      val env = Environment.simple(mode = play.api.Mode.Dev)
      val controller = new CinemaController(
        env,
        stubCinemaService,
        stubListingsService,
        stubNearbyCinemasService,
        stubControllerComponents()
      )

      controller.scriptPaths should contain("/assets/fulfilmed-scala-frontend-fastopt-bundle.js")
    }

    "generate script paths for production mode" in {
      val env = Environment.simple(mode = play.api.Mode.Prod)
      val controller = new CinemaController(
        env,
        stubCinemaService,
        stubListingsService,
        stubNearbyCinemasService,
        stubControllerComponents()
      )

      controller.scriptPaths should contain("/assets/fulfilmed-scala-frontend-opt-bundle.js")
    }

    "handle empty request body gracefully" in {
      val env = Environment.simple(mode = play.api.Mode.Test)
      val controller = new CinemaController(
        env,
        stubCinemaService,
        stubListingsService,
        stubNearbyCinemasService,
        stubControllerComponents()
      )

      val request = FakeRequest(POST, "/api/test")
      val result = controller.api("test")(request)

      status(result) shouldBe BAD_REQUEST
    }
  }
}
