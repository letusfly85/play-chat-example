package services

import akka.stream.{ ActorMaterializer, KillSwitches, UniqueKillSwitch }
import akka.stream.scaladsl.{ BroadcastHub, Flow, Keep, MergeHub, Sink, Source }
import entities.ChatEvent
import java.util.concurrent.atomic.AtomicReference

import akka.NotUsed
import akka.actor.ActorSystem

import scala.collection.mutable
import scala.concurrent.duration._

case class ChatRoom(roomId: String, bus: Flow[ChatEvent, ChatEvent, UniqueKillSwitch])

case class ChatChannel(sink: Sink[ChatEvent, NotUsed], source: Source[ChatEvent, NotUsed])

object ChatRoomFactory {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  def startRoom(roomId: String, userName: String): ChatRoom = synchronized {
    roomPool.get.get(roomId) match {
      case Some(chatRoom) =>
        chatRoom
      case None =>
        val room = create(roomId)
        roomPool.get() += (roomId -> room)
        room
    }
  }

  private def create(roomId: String): ChatRoom = {
    // Create bus parts.
    val (sink, source) =
      MergeHub.source[ChatEvent](perProducerBufferSize = 16)
        .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
        .run()

    source.runWith(Sink.ignore)

    val channel = ChatChannel(sink, source)

    val bus: Flow[ChatEvent, ChatEvent, UniqueKillSwitch] = Flow.fromSinkAndSource(channel.sink, channel.source)
      .joinMat(KillSwitches.singleBidi[ChatEvent, ChatEvent])(Keep.right)
      .backpressureTimeout(3.seconds)
      .map { e =>
        println(s"$e $channel")
        e
      }

    ChatRoom(roomId, bus)
  }

  private val rooms: scala.collection.mutable.Map[String, ChatRoom] = scala.collection.mutable.Map()

  val roomPool: AtomicReference[scala.collection.mutable.Map[String, ChatRoom]] =
    new AtomicReference[mutable.Map[String, ChatRoom]](rooms)

}

