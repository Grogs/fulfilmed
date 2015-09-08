package me.gregd.cineworld

import org.json4s.native.Serialization._
import me.gregd.cineworld.domain.{Movie, Cinema}
import org.scalatest.MustMatchers
import org.scalatra.test.scalatest.{ScalatraFunSuite, ScalatraFeatureSpec}
import org.json4s.DefaultFormats

/**
 * Author: Greg Dorrell
 * Date: 03/05/2014
 */
class SmokeTest extends ScalatraFunSuite {
  protected implicit val jsonFormats = DefaultFormats.withBigDecimal
  addServlet(Config.webservice, "/*")

  test("Get list of cinemas") {
    get("/cinema/66") {
      status should be (200)
      val movies = read[Option[List[Movie]]](body)
      movies should be ('defined)
      movies.get.size should be > 0
    }
  }
}
