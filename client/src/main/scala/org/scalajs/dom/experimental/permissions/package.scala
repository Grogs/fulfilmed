package org.scalajs.dom.experimental

import language.implicitConversions
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

/**
TODO Remove once PR merged and released: https://github.com/scala-js/scala-js-dom/pull/299
  */
package object permissions {

  @js.native
  trait PermissionState extends js.Any

  final val `granted` = "granted".asInstanceOf[PermissionState]
  final val `denied` = "denied".asInstanceOf[PermissionState]
  final val `prompt` = "prompt".asInstanceOf[PermissionState]

  @ScalaJSDefined
  trait PermissionStatus extends dom.EventTarget {
    val state: PermissionState
    val onchange: js.Function1[PermissionState, _]
  }

  @ScalaJSDefined
  trait Permissions extends js.Any {
    def query(permissionDescriptor: js.Object): js.Promise[PermissionStatus]
  }

  @ScalaJSDefined
  trait PermissionsNavigator extends js.Any {
    val permissions: Permissions
  }

  implicit def toPermissions(navigator: dom.raw.Navigator): PermissionsNavigator =
    navigator.asInstanceOf[PermissionsNavigator]

}
