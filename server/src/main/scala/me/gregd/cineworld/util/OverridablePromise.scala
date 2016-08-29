package me.gregd.cineworld.util

import scala.concurrent.{ExecutionContext, Future, Promise}

case class OverridablePromise[Res]() {
  var promise = Promise[Res]
  def completeWith(res: Future[Res])(implicit ec: ExecutionContext) =
    if (promise.isCompleted) {
      promise = Promise()
      res.onComplete(promise.complete)
    } else
      promise.completeWith(res)
  def completeWith(res: Res) =
    if (promise.isCompleted)
      promise = Promise.successful(res)
    else
      promise.success(res)
  def future = promise.future
}
