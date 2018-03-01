package entities

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

trait ChatEvent

case class Join(userId: String) extends ChatEvent

case class Leave(userId: String) extends ChatEvent

case class RequestMessage(subscribers: Option[List[String]], message: String) extends ChatEvent

case class ResponseMessage(publisher: String, requestMessage: RequestMessage) extends ChatEvent

object Join {
  implicit def joinReads: Reads[Join] = (__ \ "user_id").read[String].map(userId => Join(userId))

  implicit def joinWrites: Writes[Join] = (__ \ "user_id").write[String].contramap(join => join.userId)
}

object Leave {
  implicit def leaveReads: Reads[Leave] = (__ \ "user_id").read[String].map(userId => Leave(userId))

  implicit def leaveWrites: Writes[Leave] = (__ \ "user_id").write[String].contramap(leave => leave.userId)
}

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
