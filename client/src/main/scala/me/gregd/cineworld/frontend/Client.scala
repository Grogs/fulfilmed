package me.gregd.cineworld.frontend

import me.gregd.cineworld.domain.service.{Cinemas, ListingsService, NearbyCinemas}
import org.scalajs.dom
import sloth.{ClientException, Request, RequestTransport}

import cats.effect.IO
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

import cats.implicits._
import io.circe.generic.auto._
import chameleon.ext.circe._

object Transport extends RequestTransport[String, IO] {
  override def apply(req: Request[String]): IO[String] = {
    IO.fromFuture(
        IO(
          dom.ext.Ajax.post(
            url = "/api/" + req.path.mkString("/"),
            data = req.payload
          )))
      .map(_.responseText)
  }
}

object Client {
  private val client = sloth.Client[String, IO](Transport)

  val listings      = client.wire[ListingsService]
  val cinemas       = client.wire[Cinemas]
  val nearbyCinemas = client.wire[NearbyCinemas]
}
