package util

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.{BeforeAndAfterAll, Suite}
import play.api.libs.ws.ahc.AhcWSClient

trait WSClient extends BeforeAndAfterAll{ suite: Suite =>
  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))

  override protected def afterAll(): Unit = {
    wsClient.close()
    super.afterAll()
  }
}
