package com.td.akkasocket.myserver

import akka.actor.ActorRef
import com.td.akkasocket.myserver.actor.ProxyControllerActor.CreateClient
import com.typesafe.scalalogging.StrictLogging
import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.CharsetUtil
import spray.json.{DefaultJsonProtocol, _}

class ProxyServerHandler(controllerActor: ActorRef) extends ChannelInboundHandlerAdapter with StrictLogging {
  case class Data(value:String)
  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val myFormat = jsonFormat1(Data)
  }
  import MyJsonProtocol._

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    ctx.writeAndFlush(Unpooled.copiedBuffer( Data("Hello from SERVER").toJson.prettyPrint , CharsetUtil.UTF_8))
    controllerActor ! CreateClient(ctx)  //Create Actor when client was connected
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    logger.info("channelReadComplete")
  }

  override def channelWritabilityChanged(ctx: ChannelHandlerContext): Unit = {
    logger.info("channelWritabilityChanged")
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    logger.info("Some one disconnected")
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    logger.info("channelRead")
  }

  override def channelUnregistered(ctx: ChannelHandlerContext): Unit = {
    logger.info("channelUnregistered")
    ctx.channel().closeFuture()
    ctx.close
  }

  override def channelRegistered(ctx: ChannelHandlerContext): Unit = {
    logger.info("channelRegistered")
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
