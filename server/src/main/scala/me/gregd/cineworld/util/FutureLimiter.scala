package me.gregd.cineworld.util

import java.util.concurrent.TimeUnit

import monix.execution.Scheduler

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}

/** Based on Monix TaskLimiter from https://gist.github.com/alexandru/623fe6c587d73e89a8f14de284ca1e2d
  *
  * Request limiter for APIs that have quotas per second, minute, hour, etc.
  *
  * {{{
  *   val limiter = FutureLimiter(TimeUnit.SECONDS, limit = 100)
  *
  *   limiter.request(myTask)
  * }}}
  */
final class FutureLimiter(period: TimeUnit, limit: Int, scheduler: Scheduler) {
  import FutureLimiter.State
  import monix.execution.atomic.Atomic

  private[this] val state =
    Atomic(State(0, period, 0, limit))

  def request[A](task: => Future[A]): Future[A] = {
      val now = System.currentTimeMillis()
      state.transformAndExtract(_.request(now)) match {
        case None => task
        case Some(delay) =>
          // Recursive call, retrying request after delay
          val res = Promise[A]()
          scheduler.scheduleOnce(delay)(
            res.completeWith(request(task))
          )
          res.future
      }
    }
}

object FutureLimiter {
  /** Builder for [[FutureLimiter]]. */
  def apply(period: TimeUnit, limit: Int, scheduler: Scheduler): FutureLimiter =
    new FutureLimiter(period, limit, scheduler)

  /** Timestamp specified in milliseconds since epoch,
    * as returned by `System.currentTimeMillis`
    */
  type Timestamp = Long

  /** Internal state of [[FutureLimiter]]. */
  final case class State(window: Long, period: TimeUnit, requested: Int, limit: Int) {
    private def periodMillis =
      TimeUnit.MILLISECONDS.convert(1, period)

    def request(now: Timestamp): (Option[FiniteDuration], State) = {
      val periodMillis = this.periodMillis
      val currentWindow = now / periodMillis

      if (currentWindow != window)
        (None, copy(window = currentWindow, requested = 1))
      else if (requested < limit)
        (None, copy(requested = requested + 1))
      else {
        val nextTS = (currentWindow + 1) * periodMillis
        val sleep = nextTS - now
        (Some(sleep.millis), this)
      }
    }
  }
}