package me.gregd.cineworld.frontend

import japgolly.scalajs.react.extra.router._
import org.scalajs.dom._

object Main {

  def main(): Unit = {

    val baseUrl = BaseUrl.fromWindowOrigin + "/"

    val router = new Wiring(baseUrl).router

    router().renderIntoDOM(document.getElementById("content"))
  }

}
