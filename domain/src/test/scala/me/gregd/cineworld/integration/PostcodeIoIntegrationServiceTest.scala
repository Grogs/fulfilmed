package me.gregd.cineworld.integration

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.effect.unsafe.implicits.global
import me.gregd.cineworld.domain.model.Coordinates
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs
import util.WSClient

class PostcodeIoIntegrationServiceTest extends AnyFunSuite with ScalaFutures with IntegrationPatience with Matchers with WSClient {

  val postcodeService = new PostcodeIoIntegrationService(Stubs.postcodesio.config, wsClient)

  test("it works") {
    val examplePostcodes = Seq("OX49 5NU", "M32 0JG", "NE30 1DP")

    val expected = Map(
      "OX49 5NU" -> Coordinates(51.656143706615, -1.06986930435083),
      "M32 0JG"  -> Coordinates(53.4556572899372, -2.30283674284007),
      "NE30 1DP" -> Coordinates(55.0113051910514, -1.43926900515621),
      "AB24 5EN" -> Coordinates(57.1502699571208, -2.07796067079163),
      "AB11 5RG" -> Coordinates(57.1443735096293, -2.09607620679942),
      "GU11 1WG" -> Coordinates(51.2496276978637, -0.76918738639163)
    )

    postcodeService.lookup(examplePostcodes).unsafeRunSync() shouldBe expected
  }

}
