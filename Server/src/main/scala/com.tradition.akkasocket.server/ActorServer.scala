package com.tradition.akkasocket.server

import java.time.LocalTime

import akka.actor.{Actor, ActorSystem, Address, ExtendedActorSystem, Extension, ExtensionKey, Props}
import com.tradition.akkasocket.shared.Code
import com.tradition.akkasocket.shared.SharedMessage.{CodeRequest, JayRequest, JayResponse}
import com.typesafe.config.{Config, ConfigFactory}
import io.netty.channel.{ChannelFuture, ChannelHandlerContext}

object ActorServer {
  def generate(ctx: ChannelHandlerContext): Unit = {
    val gen = new GenConfig()
    val conf = gen.getConfig()
    val actorSystem = ActorSystem("ServerSystem", conf)
    actorSystem.actorOf(Props[ActorServer], name="server")
    val add = AddressExtension.addressOf(actorSystem)
    val port = add.port.getOrElse(0)
    println("Server is now starting Actor name : " + actorSystem.name + " on port : " + port)

    val portBuf = ctx.alloc.buffer(4)
    portBuf.writeInt(port)
    val f = ctx.writeAndFlush(portBuf)
    f.addListener((future: ChannelFuture) => {
      assert(f eq future)
      ctx.close
    })

    var msg = "Server is now started"
    do {
      println("Press 'end' to stop service")
      msg = scala.io.StdIn.readLine()
    } while (msg.toLowerCase != "end")
    actorSystem.terminate()
  }
}

class AddressExtension(system: ExtendedActorSystem) extends Extension {
  val address = system.provider.getDefaultAddress
}

object AddressExtension extends ExtensionKey[AddressExtension] {
  def addressOf(system: ActorSystem): Address = AddressExtension(system).address
}

class GenConfig(){
  def getConfig(): Config ={
    ConfigFactory.load()
  }
}

class ActorServer extends Actor {
  override def receive: Receive = {
    case JayRequest(req) if req.trim == "" =>
      println("[SERVER] server received NONE from " + sender.path.address)
      sender ! JayResponse("400 Bad Request")
    case JayRequest(req) =>
      println("[SERVER] server received str >> " + req + " << from " + sender.path.address)
      sender ! JayResponse(req)
    case CodeRequest(req) =>
      println("[SERVER] get code : " + req + " from " + sender.path.address)
      req match {
        case Code.time => sender ! JayResponse(LocalTime.now().toString)
      }

    case s:String => println(s"Unrecognized message : $s from ${sender.path.address}")
    case _ => println("Something went wrong, please report this error")
  }
}
