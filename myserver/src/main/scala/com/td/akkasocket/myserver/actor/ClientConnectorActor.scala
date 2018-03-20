package com.td.akkasocket.myserver.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.td.akkasocket.myserver.actor.ProxyControllerActor.{Heartbeat, Kill}
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.util.CharsetUtil

object ClientConnectorActor {
  def props(ctx: ChannelHandlerContext): Props = Props(new ClientConnectorActor(ctx))
}

class ClientConnectorActor(ctx: ChannelHandlerContext) extends Actor with ActorLogging {
  def receive: Receive = {
    case Heartbeat => // send heartbeat to client
      ctx.writeAndFlush(Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8))
      println("[ClientConnectorActor] Heartbeat")

    case Kill => // close client connection
      println("[ClientConnectorActor]: Kill " + sender.path.name)
      ctx.close()
  }

  override def unhandled(message: Any): Unit = {
    println("get unknown message : " + message)
  }

}

