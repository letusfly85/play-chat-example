package entities

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class Join(userId: String) extends ChatEvent

object Join {
  implicit def joinReads: Reads[Join] = (__ \ "user_id").read[String].map(userId => Join(userId))

  implicit def joinWrites: Writes[Join] = (__ \ "user_id").write[String].contramap(join => join.userId)
}
