package me.gregd.cineworld.util

import scala.collection.parallel.{ForkJoinTaskSupport, ParSeq}
import scala.concurrent.forkjoin.ForkJoinPool
import scala.util.{Failure, Success, Try}

/**
 * Created by Greg Dorrell on 26/05/2014.
 */
object Implicits {
  implicit class WithThreads[T](s:Seq[T]) {
    def threads(numThreads:Int): ParSeq[T] = {
      val parSeq = s.par
      parSeq.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool( numThreads ))
      parSeq
    }
  }

  implicit class DistinctBy[T](s:Seq[T]) {
    def distinctBy[K](func: T => K) = {
      s.groupBy(func).map(_._2.head)
    }
  }

  implicit class TryOnFailure[T](t: Try[T]) {
    def onFailure(func: Throwable => Unit) = {
      t match {
        case Success(_) =>
        case Failure(exception) => func(exception)
      }
      t
    }
  }
}
