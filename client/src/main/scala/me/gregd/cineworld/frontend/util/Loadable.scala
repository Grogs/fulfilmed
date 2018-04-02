package me.gregd.cineworld.frontend.util

sealed trait Loadable[+T]

object Loadable {
  case object Unloaded extends Loadable[Nothing]
  case object Loading extends Loadable[Nothing]
  case class Loaded[T](value: T) extends Loadable[T]
}
