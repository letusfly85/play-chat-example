package controllers

import javax.inject.{Inject, Singleton}

import actors.chat.{ChatRequestActor, ChatResponseActor}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Keep}
import entities.ChatEvent
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket
import play.api.mvc._
import services.ChatRoomFactory

@Singleton
class ChatController @Inject() (
  cc: ControllerComponents,
  implicit val system: ActorSystem,
  implicit val materializer: Materializer,
 ) extends AbstractController(cc) {

  def start(roomId: String) = WebSocket.accept[JsValue, JsValue] { implicit request =>
    val userId = request.queryString("user-id").headOption.getOrElse("unknown")

    val userInput: Flow[JsValue, ChatEvent, _] =
      ActorFlow.actorRef[JsValue, ChatEvent](out => ChatRequestActor.props(out, userId))

    val room = ChatRoomFactory.startRoom(roomId, userId)

    val userOutPut: Flow[ChatEvent, JsValue, _] =
      ActorFlow.actorRef[ChatEvent, JsValue](out => ChatResponseActor.props(out, userId))

    userInput.viaMat(room.bus)(Keep.right).viaMat(userOutPut)(Keep.right)

    /*
    val result = ActorFlow.actorRef {out =>
       MySocketActor.props(out)
    }
    result
    */
  }

}

object MySocketActor {
  def props(out: ActorRef) = Props(new MySocketActor(out))
}

class MySocketActor(out: ActorRef) extends Actor {
  def receive = {
    case out: JsValue =>
      println(out.toString())
  }
}

