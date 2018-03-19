package com.tradition.akkasocket.server

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.ReferenceCountUtil
import org.jboss.netty.channel.ChannelFuture

class DiscardServerHandler(generatePort: () => Int) extends ChannelInboundHandlerAdapter{

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    val port = generatePort()
    val portBuf = ctx.alloc.buffer(4)
    portBuf.writeInt(port)

    val t = new java.util.Timer()
    val task = new java.util.TimerTask {
      def run() = isClientAlive(ctx)
    }
    t.schedule(task, 10000L, 1000L)
    task.cancel()

    val f = ctx.writeAndFlush(portBuf)
    f.addListener((future: ChannelFuture) => {
      assert(f eq future)
      ctx.close
    })
  }

  def isClientAlive(ctx: ChannelHandlerContext): Unit ={
    try {
      println("Breading ..")
      val byteBuf = ctx.alloc.buffer(4)
      byteBuf.writeInt(999999999)
      val flush = ctx.writeAndFlush(byteBuf)
      flush.addListener((future: ChannelFuture) => {
        assert(flush eq future)
        ctx.close
      })
    } catch {
      case _: Throwable => ctx.close()
    }
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
