package me.gregd.cineworld.util

import java.util.concurrent.ConcurrentLinkedQueue

import monix.eval.Task
import monix.execution.Cancelable
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.duration._

case class TaskRateLimiter(duration: FiniteDuration, maxInvocations: Int) {

  @volatile var permits: Int = maxInvocations
  val queue = new ConcurrentLinkedQueue[() => Any]()

  global.scheduleAtFixedRate(duration, duration) {
    this synchronized {
      permits = maxInvocations

      while (!queue.isEmpty && permits > 0) {
        Option(queue.poll()).foreach { fun =>
          permits -= 1
          fun.apply()
        }
      }
    }
  }

  def apply[T](f: Task[T]): Task[T] =
    this synchronized {
      if (permits > 0) {
        permits -= 1
        f
      } else {
        Task.async[Unit] { (sched, cb) =>
          queue.add(() => { cb.onSuccess(()) })
          Cancelable.empty
        }.flatMap(_ => f)
      }
    }
}
