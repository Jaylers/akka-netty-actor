package com.td.akkasocket.myclient

import com.typesafe.scalalogging.StrictLogging
import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.CharsetUtil

class TcpClientHandler extends ChannelInboundHandlerAdapter with StrictLogging {
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    val in = msg.asInstanceOf[ByteBuf]
    val message = in.toString(CharsetUtil.UTF_8)
    logger.info("[TcpClientHandler] get message : " + message)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
