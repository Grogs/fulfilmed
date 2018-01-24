package me.gregd.cineworld.wiring

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import me.gregd.cineworld.util.{Clock, NoOpCache}
import play.api.libs.ws.ahc.AhcWSClient

class TestAppWiring(val clock: Clock) extends AppWiring with StubConfigWiring {
  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))
  val cache = NoOpCache.cache
}
