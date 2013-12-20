package me.gregd.cineworld.rest

import org.scalatra.test.scalatest.ScalatraFeatureSpec
import me.gregd.cineworld.Config
import org.scalatest.matchers.ShouldMatchers
import org.json4s.native.{Serialization, JsonMethods}
import me.gregd.cineworld.domain.Cinema
import org.json4s.{NoTypeHints, DefaultFormats}

/**
 * Author: Greg Dorrell
 * Date: 11/09/2013
 */
class CinemaServiceFeatureSpec extends ScalatraFeatureSpec with ShouldMatchers {
  import Serialization.read
  implicit val formats = Serialization.formats(NoTypeHints)

  addServlet(Config.webservice, "/*")

  feature("Cinemas list") {
    scenario("Get list of cinemas") {
      get("/cinemas") {
        status should be (200)
        val cinemasOpt = read[Option[List[Cinema]]](body)
        cinemasOpt should be ('defined)
        cinemasOpt.get exists (_.name contains "West India Quay") should be (true)
      }
    }
  }

}
