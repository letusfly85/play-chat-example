package actors.chat

import akka.actor.{ Actor, ActorRef, PoisonPill, Props }
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import entities.{ Join, Leave, SendMessage }
import play.api.Logger
import play.api.libs.json.JsValue
import play.libs.Json

class ChatRequestActor(out: ActorRef, userId: String) extends Actor {

  def receive = {
    case message: ObjectNode =>
      out ! SendMessage(userId = userId, message = message.toString)

    case message: JsValue =>
      Logger.info(message.toString())
      out ! message.as[SendMessage]
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

case class Simplemessage(message: String)
class ChatResponseActor(out: ActorRef, currentId: String) extends Actor {

  import com.fasterxml.jackson.databind.ObjectMapper

  val mapper = new ObjectMapper

  def receive = {
    case message: ObjectNode =>
      out ! SendMessage(userId = currentId, message = message.toString)

    case SendMessage(u, t) =>
      Logger.info(t)
      val objectNode = mapper.createObjectNode()
      objectNode.put("message", t)

      out ! objectNode

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
