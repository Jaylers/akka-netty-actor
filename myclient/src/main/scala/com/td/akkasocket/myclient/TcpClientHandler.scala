package com.td.akkasocket.myclient

import com.typesafe.scalalogging.StrictLogging
import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.CharsetUtil

class TcpClientHandler extends ChannelInboundHandlerAdapter with StrictLogging {
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    val in = msg.asInstanceOf[ByteBuf]
    val message = in.toString(CharsetUtil.UTF_8)
    message match {//message from server
      case "Heartbeat" => logger.info("[TCH] Get : " + message)
      case _ => logger.info("[TCH] get unknown : " + message)
    }
  }


  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    logger.info("channelReadComplete" + ctx.channel().id())
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    logger.info("Something wrong, We got killed!")
    ctx.close()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
