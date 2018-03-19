package com.tradion.akkasocket.client

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}

class TcpClientHandler extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    val bufMessage = msg.asInstanceOf[ByteBuf]

    val buf: ByteBuf = ctx.alloc.buffer(4)
    val byteBuf: ByteBuf = buf.writeBytes(bufMessage)

    bufMessage.release
    if (byteBuf.readableBytes >= 4) {
      val message: java.lang.String = byteBuf.toString
      println("[TcpClientHandler] get message : " + message)
      ctx.close
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close
  }
}
