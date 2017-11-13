package me.gregd.cineworld.frontend.services

import org.scalajs.dom.experimental.permissions._
import org.scalajs.dom.experimental.permissions.PermissionName.geolocation
import org.scalajs.dom.window
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

import scala.concurrent.Future

object Geolocation {
  def havePermission(): Future[Boolean] = {
    window.navigator.permissions
      .query(PermissionDescriptor(geolocation))
      .toFuture
      .map(s => s.state == PermissionState.granted)
  }
}
