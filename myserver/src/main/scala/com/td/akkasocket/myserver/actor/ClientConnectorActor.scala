package com.td.akkasocket.myserver.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tradition.akkasocket.shared.Code.{Heartbeat, Kill, News}
import io.netty.channel.ChannelHandlerContext
import io.netty.util.concurrent.Future
import spray.json._

object ClientConnectorActor {
  def props(ctx: ChannelHandlerContext): Props = Props(new ClientConnectorActor(ctx))
}

class ClientConnectorActor(ctx: ChannelHandlerContext) extends Actor with ActorLogging {
  case class Data(value:String)
  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val myFormat: RootJsonFormat[Data] = jsonFormat1(Data)
  }
  import MyJsonProtocol._

  ctx.channel.closeFuture.addListener((_: Future[Void]) => { // if the coming ctx is disconnected Client
    log.info("ClientConnectorActor ctx channel closeFuture -> OperationComplete")
    context.stop(self)
  })

  def receive: Receive = {
    case Heartbeat => // send heartbeat to client
      val json = Data("Heartbeat").toJson
      ctx.writeAndFlush(json.prettyPrint)

    case int:Int =>
      ctx.writeAndFlush(int.toString)
      log.info("Now, we have " + int + " people connected")

    case News =>
      val json = Data("There are few things I regret more than my familiarity with the Trump family. Over the course of " +
        "the past year or two, I have come to know each of them in their own horrible way, like relatives you dread seeing " +
        "at Thanksgiving. I know about Melania’s taste in decor, evidenced by those Nightmare Before Christmas horror-trees" +
        " in the White House hallway. I have skimmed Ivanka’s Instagram and come to know the millennial-pink taste of " +
        "terror. And I know — as every single one of us must know by now — that Donald Trump Jr., the president’s largest" +
        " and most adult of sons, is getting a divorce from his wife, Vanessa. \nAll of this raises an inevitable debate" +
        " about how, as members of the media, we ought to cover the Trump family. And right now, we are being asked to be" +
        " kind about Don’s marital woes — namely, the reports that the mother of his children is reportedly leaving him" +
        " because she can’t stand his “combative public persona,” and also because he cheated on her with a contestant from").toJson
      ctx.writeAndFlush(json.prettyPrint+"")
      log.info("[ClientConnectorActor] News Announce")

    case Kill => // close client connection
      log.info("[ClientConnectorActor]: channel id : " + ctx.channel().id() + " is gone")
      ctx.close()
  }

  override def unhandled(message: Any): Unit = {
    log.info("get unknown message : " + message)
  }

  override def postStop(): Unit = {
    log.info("ClientConnectorActor: post stop")
  }
}

