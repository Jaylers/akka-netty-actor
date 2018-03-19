package com.tradition.akkasocket.server

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.ReferenceCountUtil

class DiscardServerHandler extends ChannelInboundHandlerAdapter{

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    println("Channel status : ACTIVE")
    ActorServer.generate(ctx)
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Object): Unit = {
    import io.netty.buffer.ByteBuf
    msg.asInstanceOf[ByteBuf].release
    try {
      println(msg.toString)
    } finally {
      ReferenceCountUtil.release(msg)
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
