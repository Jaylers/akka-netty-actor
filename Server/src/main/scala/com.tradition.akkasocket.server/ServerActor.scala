package com.tradition.akkasocket.server

import akka.actor.{Actor, ActorSystem, Props}
import com.tradition.akkasocket.shared.SharedMessage.{JayRequest, JayResponse}
import com.typesafe.config.ConfigFactory

object ServerActor{
  def main(args: Array[String]): Unit ={
    val system = ActorSystem("ServerSystem", ConfigFactory.load)
    system.actorOf(Props[ServerActor], name="server")
    var msg = "Server is now started"
    println(msg)
    do {
      println("Press 'end' to stop service")
      msg = scala.io.StdIn.readLine()
    } while (msg.toLowerCase != "end")
    system.terminate()
  }
}

class ServerActor extends Actor {
  override def receive: Receive = {
    case JayRequest(req) if req.trim == "" =>
      println("[SERVER] server received NONE from " + sender.path.address)
      sender ! JayResponse("400 Bad Request")

    case JayRequest(req) =>
      println("[SERVER] server received str >> " + req + " << from " + sender.path.address)
      sender ! JayResponse("Server got : "+req)

    case _ => println("Something went wrong, please report this error")
  }
}