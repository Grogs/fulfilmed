package me.gregd.cineworld.messenger

import javax.inject.{Inject, Named => named}

import cats.data.Xor
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import concurrent.ExecutionContext.Implicits.global


class FacebookSender @Inject() (@named("facebook.page-access-token") pageAccessToken: String, wsClient: WSClient) {

  val url = s"https://graph.facebook.com/v2.6/me/messages?access_token=$pageAccessToken"

  type Button = Unit
  type Request = Unit
  type Response = Unit

  implicit val encoder = Encoder[Button]

  def send(request: Request): Future[Response] = {
    val json = request.asJson.noSpaces
    wsClient
      .url(url)
      .post(json)
      .map( res =>
        decode[Response](res.body) match {
          case Xor.Left(e) => throw e
          case Xor.Right(r) => r
        }
      )
  }

}
