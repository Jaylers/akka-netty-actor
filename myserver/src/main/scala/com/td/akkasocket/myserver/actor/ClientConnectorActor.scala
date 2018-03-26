package com.td.akkasocket.myserver.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tradition.akkasocket.shared.Code.{Heartbeat, Kill, News}
import io.netty.channel.ChannelHandlerContext
import io.netty.util.concurrent.Future
import spray.json._

object ClientConnectorActor {
  def props(ctx: ChannelHandlerContext): Props = Props(new ClientConnectorActor(ctx))
}

class ClientConnectorActor(ctx: ChannelHandlerContext) extends Actor with ActorLogging {
  case class Data(value:String)
  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val myFormat: RootJsonFormat[Data] = jsonFormat1(Data)
  }
  import MyJsonProtocol._

  ctx.channel.closeFuture.addListener((_: Future[Void]) => { // if the coming ctx is disconnected Client
    log.info("ClientConnectorActor ctx channel closeFuture -> OperationComplete")
    context.stop(self)
  })

  def receive: Receive = {
    case Heartbeat => // send heartbeat to client
      val json = Data("Heartbeat").toJson
//      ctx.writeAndFlush(Unpooled.copiedBuffer( json.prettyPrint + "/", CharsetUtil.UTF_8))
      ctx.writeAndFlush(json.prettyPrint+ "/")

    case int:Int =>
//      ctx.writeAndFlush(Unpooled.copiedBuffer( int + "/", CharsetUtil.UTF_8))
      ctx.writeAndFlush(int + "/")
      log.info("Now, we have " + int + " people connected")

    case News =>
      val json = Data("Author’s Note: For at least a decade and if not more, there has been a serial killer in Toronto," +
        " preying upon South Asian and Middle Eastern men associated with the queer village. The tragedy has shaken " +
        "community members to the core. We remember the risk of isolation. We remember the value of family. We remember " +
        "the fragility of our tender bodies. We remember justice is elusive. And that love is even more so. Months later, " +
        "we all find out Bruce McArthur has been charged with five counts of first degree murder and no bodies. Between the" +
        " first and second drafts of this article, the number climbed to seven bodies and six charges. One man remains" +
        " unidentified. Abdulbasir remains missing.").toJson
//      ctx.writeAndFlush(Unpooled.copiedBuffer( json.prettyPrint + "/", CharsetUtil.UTF_8))
      ctx.writeAndFlush(json.prettyPrint+"/")
      log.info("[ClientConnectorActor] News Announce")

    case Kill => // close client connection
      log.info("[ClientConnectorActor]: channel id : " + ctx.channel().id() + " is gone")
      ctx.close()
  }

  override def unhandled(message: Any): Unit = {
    log.info("get unknown message : " + message)
  }

  override def postStop(): Unit = {
    log.info("ClientConnectorActor: post stop")
  }
}

