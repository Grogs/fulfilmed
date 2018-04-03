package me.gregd.cineworld

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.ws.ahc.AhcWSClient

class PostcodeServiceTest extends FunSuite with ScalaFutures with Matchers {
  private val actorSystem = ActorSystem()

  val wsClient = AhcWSClient()(ActorMaterializer()(actorSystem))

  val postcodeService = new PostcodeService("", wsClient)

  test("it works") {
    postcodeService.lookup(Seq("OX49 5NU", "M32 0JG", "NE30 1DP"))
  }

}
