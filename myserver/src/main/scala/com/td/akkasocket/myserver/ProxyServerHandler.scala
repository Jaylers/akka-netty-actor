package com.td.akkasocket.myserver

import akka.actor.ActorRef
import com.td.akkasocket.myserver.actor.ProxyControllerActor.CreateClient
import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.CharsetUtil

class ProxyServerHandler(controllerActor: ActorRef) extends ChannelInboundHandlerAdapter {
  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    ctx.writeAndFlush(Unpooled.copiedBuffer("Hello from server", CharsetUtil.UTF_8))
    controllerActor ! CreateClient(ctx)  //Create Actor when client was connected
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
