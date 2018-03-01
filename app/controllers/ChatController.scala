package controllers

import javax.inject.{Inject, Singleton}

import actors.chat.{ChatRequestActor, ChatResponseActor}
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Keep}
import entities.ChatEvent
import play.api.libs.json.JsValue
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
  }

}
