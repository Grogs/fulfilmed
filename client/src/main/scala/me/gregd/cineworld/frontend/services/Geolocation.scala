package me.gregd.cineworld.frontend.services

import me.gregd.cineworld.domain.model.Coordinates
import org.scalajs.dom.experimental.permissions._
import org.scalajs.dom.experimental.permissions.PermissionName.geolocation
import org.scalajs.dom.raw.Position
import org.scalajs.dom.window
import org.scalajs.dom.window.navigator

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.concurrent.{Future, Promise}

object Geolocation {
  def havePermission(): Future[Boolean] = {
    window.navigator.permissions
      .query(PermissionDescriptor(geolocation))
      .toFuture
      .map(s => s.state == PermissionState.granted)
  }

  def getCurrentPosition(): Future[Coordinates] = {
    val location = Promise[Position]()
    navigator.geolocation.getCurrentPosition(p => location.success(p), err => location.failure(new Exception(err.message)))
    location.future.map(pos =>
      Coordinates(pos.coords.latitude, pos.coords.longitude)
    )
  }
}
