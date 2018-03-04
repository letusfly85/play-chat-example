package entities

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class RequestMessage(subscribers: Option[List[String]], message: String) extends ChatEvent

object RequestMessage {
  implicit def requestMessageReads: Reads[RequestMessage] = (
    (JsPath \ "subscribers").readNullable[List[String]] and
    (JsPath \ "message").read[String]
  )(RequestMessage.apply _)

  implicit def requestMessageWrites: Writes[RequestMessage] = (
    (JsPath \ "subscribers").writeNullable[List[String]] and
    (JsPath \ "message").write[String]
  )(unlift(RequestMessage.unapply))
}
