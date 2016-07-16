package me.gregd.cineworld.messenger

import javax.inject.{Inject, Named => named}

import play.api.mvc.{Action, ResponseHeader, Results}
import Results.{Forbidden, InternalServerError, Ok}
import cats.data.Xor
import grizzled.slf4j.Logging
import io.circe.parser._

import scala.concurrent.ExecutionContext

class WebhookController @Inject()(
                                   @named("facebook.validation-token") validationToken: String,
                                   messageProcessor: MessageProcessor,
                                   facebookSender: FacebookSender
) extends Logging {

  implicit val ec = ExecutionContext.global

  type MessagingEvent = Unit

  def webhook() = Action { request =>
    val queryParam = request.getQueryString(_:String).get
    if (queryParam("hub.mode") == "subscribe" && queryParam("hub.verify_token") == validationToken)
      Ok(queryParam("hub.challenge"))
    else request.body.asText match {
      case Some(body) =>
        decode[MessagingEvent](body) match {
          case Xor.Right(messagingEvent) =>
            messageProcessor.process(messagingEvent).map(_.transform(
              facebookSender.send,
              ex => {logger.error("Failed to send message to facebook.", ex); ex}
            ))
            Ok
          case Xor.Left(error) =>
            logger.error(s"Failed to parse Facebook webhook data. Cause: $error", error)
            InternalServerError
        }
      case None =>
        Forbidden
    }
  }

}
