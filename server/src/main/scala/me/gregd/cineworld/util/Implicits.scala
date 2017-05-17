package me.gregd.cineworld.util

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object Implicits {

  implicit class FutureOrElse[T](f1: Future[T]) {
    import scala.concurrent.ExecutionContext.Implicits.global
    def orElse(f2: => Future[T]): Future[T] = {
      if (f1.isCompleted && f1.value.get.isFailure) {
        f2
      } else {
        f1 recoverWith { case _ => f2 }
      }
    }
  }

}
