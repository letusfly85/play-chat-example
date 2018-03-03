package entities

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class Leave(userId: String) extends ChatEvent

object Leave {
  implicit def leaveReads: Reads[Leave] = (__ \ "user_id").read[String].map(userId => Leave(userId))

  implicit def leaveWrites: Writes[Leave] = (__ \ "user_id").write[String].contramap(leave => leave.userId)
}
