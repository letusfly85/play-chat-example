package entities

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class ResponseMessage(publisher: String, requestMessage: RequestMessage) extends ChatEvent

object ResponseMessage {
  implicit def responseMessageReads: Reads[ResponseMessage] = (
    (JsPath \ "publisher").read[String] and
      (JsPath \ "request_message").read[RequestMessage]
    )(ResponseMessage.apply _)

  implicit def responseMessageWrites: Writes[ResponseMessage] = (
    (JsPath \ "publisher").write[String] and
      (JsPath \ "request_message").write[RequestMessage]
    )(unlift(ResponseMessage.unapply))
}
