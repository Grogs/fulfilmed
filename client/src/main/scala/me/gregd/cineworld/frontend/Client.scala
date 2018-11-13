package me.gregd.cineworld.frontend

import me.gregd.cineworld.domain.service.{Cinemas, Listings, NearbyCinemas}
import org.scalajs.dom
import sloth.{ClientException, Request, RequestTransport}

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

import cats.implicits._
import io.circe.generic.auto._
import chameleon.ext.circe._

object Transport extends RequestTransport[String, Future]{
  override def apply(req: Request[String]): Future[String] = {
    dom.ext.Ajax.post(
      url = "/api/" + req.path.mkString("/"),
      data = req.payload
    ).map(_.responseText)
  }
}

object Client {
  private val client = sloth.Client[String, Future, ClientException](Transport)

  val listings = client.wire[Listings[Future]]
  val cinemas = client.wire[Cinemas[Future]]
  val nearbyCinemas = client.wire[NearbyCinemas[Future]]
}