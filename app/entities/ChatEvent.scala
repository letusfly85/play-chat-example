package entities

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

trait ChatEvent

case class Join(userId: String) extends ChatEvent

case class Leave(userId: String) extends ChatEvent

case class SendMessage(userId: String, message: String) extends ChatEvent

object Join {
  implicit def joinReads: Reads[Join] = (__ \ "user_id").read[String].map(userId => Join(userId))

  implicit def joinWrites: Writes[Join] = (__ \ "user_id").write[String].contramap(join => join.userId)
}

object Leave {
  implicit def leaveReads: Reads[Leave] = (__ \ "user_id").read[String].map(userId => Leave(userId))

  implicit def leaveWrites: Writes[Leave] = (__ \ "user_id").write[String].contramap(leave => leave.userId)
}

object SendMessage {
  implicit def sendMessageReads: Reads[SendMessage] = (
    (JsPath \ "user_id").read[String] and
    (JsPath \ "message").read[String]
  )(SendMessage.apply _)

  implicit def sendMessageWrites: Writes[SendMessage] = (
    (JsPath \ "user_id").write[String] and
    (JsPath \ "message").write[String]
  )(unlift(SendMessage.unapply))
}
