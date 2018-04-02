package me.gregd.cineworld.frontend.util

import org.scalajs.dom.History

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("history", JSImport.Default)
@js.native
object History extends js.Object {
  def createBrowserHistory(): RichHistory = js.native
}

@js.native
trait History extends js.Object {
  def push(path: String): Unit
}

@js.native
trait RichHistory extends History {
  def listen(listener: js.Function0[Unit]): Unit = js.native
}
