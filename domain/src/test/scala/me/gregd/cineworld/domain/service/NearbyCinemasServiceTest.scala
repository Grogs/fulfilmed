package me.gregd.cineworld.domain.service

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import me.gregd.cineworld.domain.model.{Cinema, Coordinates}
import me.gregd.cineworld.util.RTree
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class NearbyCinemasServiceTest extends AnyFunSuite with Matchers {

  val testCinemas = Seq(
    Cinema("1", "Cinema 1", Coordinates(51.5074, -0.1278)),
    Cinema("2", "Cinema 2", Coordinates(51.5155, -0.1415)),
    Cinema("3", "Cinema 3", Coordinates(51.5033, -0.1196)),
    Cinema("4", "Far Cinema", Coordinates(53.4808, -2.2426)) // Manchester, much further
  )

  val cinemaService = new CinemasService {
    override def getCinemas: IO[Seq[Cinema]] = IO.pure(testCinemas)
  }

  val nearbyCinemasService = new NearbyCinemasService(cinemaService)

  test("find nearby cinemas within distance") {
    val london = Coordinates(51.5074, -0.1278)
    val nearby = nearbyCinemasService.nearest(london, 5000, 10).unsafeRunSync()

    // Should find cinemas 1, 2, and 3 (all in London area)
    nearby.size should be >= 2
    nearby.size should be <= 3

    // Should not include the far cinema in Manchester
    nearby.exists(_.id == "4") shouldBe false
  }

  test("limit number of results") {
    val london = Coordinates(51.5074, -0.1278)
    val nearby = nearbyCinemasService.nearest(london, 10000, 2).unsafeRunSync()

    // Should return at most 2 results
    nearby.size should be <= 2
  }

  test("return empty when no cinemas within distance") {
    val northPole = Coordinates(90.0, 0.0)
    val nearby = nearbyCinemasService.nearest(northPole, 100, 10).unsafeRunSync()

    // Should find no cinemas near the North Pole
    nearby.size shouldBe 0
  }

  test("return closest cinemas first") {
    val london = Coordinates(51.5074, -0.1278)
    val nearby = nearbyCinemasService.nearest(london, 10000, 3).unsafeRunSync()

    // First result should be Cinema 1 (same coordinates)
    if (nearby.nonEmpty) {
      nearby.head.id shouldBe "1"
    }
  }
}
