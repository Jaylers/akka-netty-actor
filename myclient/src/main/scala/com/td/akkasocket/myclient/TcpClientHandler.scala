package com.td.akkasocket.myclient

import com.typesafe.scalalogging.StrictLogging
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import spray.json._

class TcpClientHandler extends ChannelInboundHandlerAdapter with StrictLogging {
  case class Data(value:String)
  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val myFormat: RootJsonFormat[Data] = jsonFormat1(Data)
  }

  import MyJsonProtocol._
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    val data = getData(msg.toString)
    data.value match {//message from server
      case "Heartbeat" => logger.info("[TCH] Get : " + data.value)
      case "Hello from SERVER" => logger.info(data.value)
      case x => if(isInt(x)) logger.info("[TCH] server pupulation : " + data.value + " people")
                else logger.info("[TCH] get unknown : " + data.value)
    }
  }

  def getData(str: String):Data = {
    try {
      val json = str.parseJson
      json.convertTo[Data]
    } catch { case _: Throwable => Data(str) }
  }

  def isInt(str: String):Boolean = { try { str.toInt } catch { case _: Throwable => return false }; true }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    logger.info("channelReadComplete")
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