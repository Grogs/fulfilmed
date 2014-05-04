package me.gregd.cineworld

import org.json4s.native.Serialization._
import me.gregd.cineworld.domain.{Movie, Cinema}
import org.scalatra.test.scalatest.{ScalatraFunSuite, ScalatraFeatureSpec}
import org.scalatest.matchers.ShouldMatchers
import org.json4s.DefaultFormats

/**
 * Author: Greg Dorrell
 * Date: 03/05/2014
 */
class SmokeTest extends ScalatraFunSuite with ShouldMatchers {
  protected implicit val jsonFormats = DefaultFormats.withBigDecimal
  addServlet(Config.webservice, "/*")

  test("Get list of cinemas") {
    get("/cinema/66") {
      status must be (200)
      val movies = read[Option[List[Movie]]](body)
      movies must be ('defined)
      movies.get.size must be > (0)
    }
  }
}
