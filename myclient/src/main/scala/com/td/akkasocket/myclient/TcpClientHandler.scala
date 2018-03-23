package com.td.akkasocket.myclient

import com.typesafe.scalalogging.StrictLogging
import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.CharsetUtil
import spray.json._

class TcpClientHandler extends ChannelInboundHandlerAdapter with StrictLogging {
  case class Data(value:String)
  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val myFormat = jsonFormat1(Data)
  }

  import MyJsonProtocol._
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    val in = msg.asInstanceOf[ByteBuf]
    val message = in.toString(CharsetUtil.UTF_8) //{"value": "xxxxxxx"}
    val data = getData(message)
    data.value match {//message from server
      case "Heartbeat" => logger.info("[TCH] Get : " + data.value)
      case "Hello from SERVER" => logger.info(data.value)
      case _ => logger.info("[TCH] get unknown : " + message +"  ")
    }
  }

  def getData(message: String):Data = {
    try {
      val json = message.parseJson
      json.convertTo[Data]
    } catch  {
      case _: Throwable => Data(message)
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
