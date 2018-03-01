package actors.chat

import akka.actor.{ Actor, ActorRef, PoisonPill, Props }
import entities.{ Join, Leave, SendMessage }
import play.api.Logger
import play.api.libs.json.{ JsError, JsSuccess, JsValue, Json }

class ChatRequestActor(out: ActorRef, userId: String) extends Actor {

  def receive = {
    case message: JsValue =>
      Logger.info(message.toString())

      Json.fromJson[SendMessage](message) match {
        case JsSuccess(sendMessage, _) =>
          out ! SendMessage(userId = userId, message = sendMessage.message)

        case JsError(error) =>
          out ! SendMessage(userId = userId, message = error.toString())
      }
  }

  override def preStart(): Unit = {
    Logger.info(userId)
    out ! Join(userId)
  }

  override def postStop(): Unit = {
    out ! Leave(userId)
    out ! PoisonPill
  }
}

object ChatRequestActor {
  def props(out: ActorRef, userId: String): Props = Props(new ChatRequestActor(out, userId))
}

class ChatResponseActor(out: ActorRef, currentId: String) extends Actor {
  def receive = {
    case message: SendMessage =>
      out ! Json.toJson(message)

    case Join(userId) =>
      out ! Json.toJson(Map("user_id" -> userId))

    case Leave(userId) =>
      out ! Json.toJson(Map("user_id" -> userId))

      if (userId == currentId) {
        out ! PoisonPill
        self ! PoisonPill
      }

    case some =>
      Logger.info(some.getClass.getName)
      Logger.info(some.toString)
  }

  override def postStop(): Unit = super.postStop()
}

object ChatResponseActor {
  def props(out: ActorRef, me: String): Props = Props(new ChatResponseActor(out, me))
}
