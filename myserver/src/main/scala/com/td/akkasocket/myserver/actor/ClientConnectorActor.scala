package com.td.akkasocket.myserver.actor

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import com.tradition.akkasocket.shared.Code.{Heartbeat, Kill}
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.util.CharsetUtil
import io.netty.util.concurrent.Future

object ClientConnectorActor {
  def props(ctx: ChannelHandlerContext): Props = Props(new ClientConnectorActor(ctx))
}

class ClientConnectorActor(ctx: ChannelHandlerContext) extends Actor with ActorLogging {

  ctx.channel.closeFuture.addListener((_: Future[Void]) => { // if the coming ctx is disconnected Client
    log.info("ClientConnectorActor ctx channel closeFuture -> OperationComplete")
    context.stop(self)
  })

  def receive: Receive = {
    case Heartbeat => // send heartbeat to client
      ctx.writeAndFlush(Unpooled.copiedBuffer( Heartbeat.toString , CharsetUtil.UTF_8))
      log.info("[ClientConnectorActor] "+ Heartbeat)

    case Kill => // close client connection
      log.info("[ClientConnectorActor]: channel id : " + ctx.channel().id() + " gone")
      ctx.close()
  }

  override def unhandled(message: Any): Unit = {
    log.info("get unknown message : " + message)
  }

  override def postStop(): Unit = {
    log.info("ClientConnectorActor: post stop")
  }
}

