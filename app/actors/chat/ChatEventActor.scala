package actors.chat

import akka.actor.{ Actor, ActorRef, PoisonPill, Props }
import entities.{ Join, Leave, SendMessage }
import play.api.libs.json.JsValue
import play.libs.Json

class ChatRequestActor(out: ActorRef, userName: String) extends Actor {

  def receive = {
    case message: JsValue =>
      out ! message.as[SendMessage]
  }

  override def preStart(): Unit = out ! Join(userName)

  override def postStop(): Unit = {
    out ! Leave(userName)
    out ! PoisonPill
  }
}

object ChatRequestActor {
  def props(out: ActorRef, userName: String): Props = Props(new ChatRequestActor(out, userName))
}

class ChatResponseActor(out: ActorRef, currentId: String) extends Actor {

  def receive = {
    case SendMessage(u, s, t) =>
      out ! Json.toJson(SendMessage(u, s, t))
    case Join(userId) =>
      out ! Json.toJson(Map("user_id" -> userId))
    case Leave(userId) =>
      out ! Json.toJson(Map("user_id" -> userId))
      if (userId == currentId) {
        out ! PoisonPill
        self ! PoisonPill
      }
  }

  override def postStop(): Unit = super.postStop()

}

object ChatResponseActor {
  def props(out: ActorRef, me: String): Props = Props(new ChatResponseActor(out, me))
}
