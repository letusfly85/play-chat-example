package entities

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

trait ChatEvent

case class Join(userId: String) extends ChatEvent

case class Leave(userId: String) extends ChatEvent

// case class SendMessage(userId: String, subscribers: Option[List[String]], message: String) extends ChatEvent
case class SendMessage(userId: String, message: String) extends ChatEvent

object SendMessage {
  implicit def articleEntityReads: Reads[SendMessage] = (
    (JsPath \ "user_id").read[String] and
    // (JsPath \ "subscribers").readNullable[List[String]] and
    (JsPath \ "message").read[String]
  )(SendMessage.apply _)

  implicit def articleEntityWrites: Writes[SendMessage] = (
    (JsPath \ "user_id").write[String] and
    // (JsPath \ "subscribers").writeNullable[List[String]] and
    (JsPath \ "message").write[String]
  )(unlift(SendMessage.unapply))
}
