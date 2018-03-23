package com.td.akkasocket.myserver.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tradition.akkasocket.shared.Code.{Born, Heartbeat, Kill}
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.util.CharsetUtil
import io.netty.util.concurrent.Future
import spray.json._

object ClientConnectorActor {
  def props(ctx: ChannelHandlerContext): Props = Props(new ClientConnectorActor(ctx))
}

class ClientConnectorActor(ctx: ChannelHandlerContext) extends Actor with ActorLogging {
  case class Data(value:String)
  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val myFormat = jsonFormat1(Data)
  }
  import MyJsonProtocol._

  ctx.channel.closeFuture.addListener((_: Future[Void]) => { // if the coming ctx is disconnected Client
    log.info("ClientConnectorActor ctx channel closeFuture -> OperationComplete")
    context.stop(self)
  })

  def receive: Receive = {
    case Heartbeat => // send heartbeat to client
      val json = Data("Heartbeat").toJson
      val data:Data = json.convertTo[Data]
      ctx.writeAndFlush(Unpooled.copiedBuffer( json.prettyPrint + "|", CharsetUtil.UTF_8))
      log.info("[ClientConnectorActor] sending => " + data.value)

    case int:Int =>
      ctx.writeAndFlush(Unpooled.copiedBuffer( int + "|", CharsetUtil.UTF_8))
      log.info("[ClientConnectorActor] We have => " + int + " people connected")

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

