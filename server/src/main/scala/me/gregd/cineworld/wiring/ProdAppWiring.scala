package me.gregd.cineworld.wiring

import me.gregd.cineworld.util.{Clock, RealClock}
import play.api.Mode
import play.api.Mode.Prod
import play.api.libs.ws.WSClient

class ProdAppWiring(val wsClient: WSClient) extends AppWiring with CacheWiring with TypesafeConfigWiring {
  val clock: Clock = RealClock
  val mode: Mode = Prod
}