package me.gregd.cineworld.messenger

import javax.inject.Inject

import me.gregd.cineworld.dao.cineworld.CineworldDao

import scala.concurrent.Future

class MessageProcessor @Inject() (dao: CineworldDao) {

  type MessagingEvent = Unit
  type Request = Unit
  type Recipient = Unit
  type PostbackPayload = Unit
  type Message = Unit
  type TextMessage = Unit

  var conversations = Map.empty.withDefault(Conversation.apply)

  def process(messagingEvent: MessagingEvent): Seq[Future[Request]] =
    null
//    messagingEvent match {
//      case MessageReceived(sender, _, _,message) =>
//        val to = FacebookId(sender.id)
//        val convo = conversations(to)
//        val resp = convo.send(message.text)
//        Seq(Future.successful(
//          MessageRequest(to, resp)
//        ))
//      case Postback(sender, received, timestamp, postback) =>
//        val to = FacebookId(sender.id)
//        val convo = conversations(to)
//        convo.process(postback)
//        Nil
//    }

  case class Conversation(recipient: Recipient) {
    def process(callback: PostbackPayload): Message = ???

    def send(text: String): Message = ()//TextMessage(text)

  }
}

