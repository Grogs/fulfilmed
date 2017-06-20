package me.gregd.cineworld.util

import java.util.concurrent.ConcurrentLinkedQueue

import monix.execution.Scheduler.Implicits.global
import monix.execution.atomic.AtomicInt

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._

case class RateLimiter(duration: FiniteDuration, maxInvocations: Int) {


  val permits = AtomicInt(maxInvocations)
  val queue = new ConcurrentLinkedQueue[() => Any]()

  global.scheduleAtFixedRate(duration, duration) {
    permits.set(maxInvocations)

    while(!queue.isEmpty && permits.get > 0) {
      Option(queue.poll()).foreach{ fun =>
        permits.decrement()
        fun.apply()
      }
    }
  }

  def apply[T](f: => Future[T]): Future[T] = {
    if (permits.get > 0) {
      permits -= 1
      f
    } else {
      val res = Promise[T]()
      queue.add(() => {res.completeWith(f)})
      res.future
    }
  }
}
