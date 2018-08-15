package me.gregd.cineworld.frontend

import io.circe.{Decoder, Encoder, Json}
import io.circe.syntax._
import io.circe.parser._
import org.scalajs.dom

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue


object Client extends autowire.Client[Json, Decoder, Encoder]{
  override def doCall(req: Request): Future[Json] = {
    dom.ext.Ajax.post(
      url = "/api/" + req.path.mkString("/"),
      data = req.args.asJson.noSpaces
    ).map(_.responseText).map(parse(_).toTry.get)
  }
  def read[Result: Decoder](p: Json) = p.as[Result].toTry.get
  def write[Result: Encoder](r: Result) = r.asJson
}
