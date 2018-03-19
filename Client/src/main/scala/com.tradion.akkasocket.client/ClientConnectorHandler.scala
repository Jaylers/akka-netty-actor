package com.tradion.akkasocket.client

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.ReferenceCountUtil
import org.jboss.netty.channel.ChannelFuture

class ClientConnectorHandler() extends ChannelInboundHandlerAdapter{
  import io.netty.buffer.ByteBuf
  import io.netty.channel.ChannelHandlerContext

  private var buf: ByteBuf = _


  override def handlerAdded(ctx: ChannelHandlerContext): Unit = {
    buf = ctx.alloc.buffer(4)
  }

  override def handlerRemoved(ctx: ChannelHandlerContext): Unit = {
    buf.release
    buf = null
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    val message = msg.asInstanceOf[ByteBuf]
    buf.writeBytes(message)

    message.release
    if (buf.readableBytes >= 3) {
      val message = buf.readInt()
      if(message == 200){
        println("[ClientConnectorHandler] Connection status : " + message)
      } else if (buf.readableBytes() >= 4){
        ActorClient.generate(message)
      }

      ctx.close
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close
  }
}
