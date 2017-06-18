package me.gregd.cineworld.util

import java.util.concurrent.ConcurrentLinkedQueue

import monix.execution.Scheduler.Implicits.global

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._

case class RateLimiter(duration: FiniteDuration, maxInvocations: Int) {


  @volatile var permits = maxInvocations
  val queue = new ConcurrentLinkedQueue[() => Any]()

  global.scheduleAtFixedRate(duration, duration) {
    permits = maxInvocations
    while(!queue.isEmpty && permits > 0) {
      permits -= 1
      val next = Option(queue.poll())
      next.foreach{ fun =>
        fun.apply()
      }
    }
  }

  def apply[T](f: => Future[T]): Future[T] = {
    if (permits > 0) {
      permits -= 1
      f
    } else {
      val res = Promise[T]()
      queue.add(() => {res.completeWith(f)})
      res.future
    }
  }
}
