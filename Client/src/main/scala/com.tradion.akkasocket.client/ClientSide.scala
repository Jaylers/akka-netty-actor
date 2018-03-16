package com.tradion.akkasocket.client

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.tradition.akkasocket.shared.SharedMessage.{JayRequest, JayResponse}
import com.typesafe.config.ConfigFactory

object ClientSide {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("ClientSystem", ConfigFactory.load)
    val clientActor = system.actorOf(Props[ClientSide], name="client")
    var msg = "Client is started on : " + clientActor.path.address
    do {
      clientActor ! JayRequest(msg)
      msg = scala.io.StdIn.readLine()
    } while (msg.toUpperCase() != "END")
    system.terminate()
  }
}

class ClientSide extends Actor {

  val path = "akka.tcp://server@127.0.0.1:2500/user/server"
  val serverActor: ActorSelection = context.actorSelection(path)

  override def receive: Receive = {
    case req: JayRequest => serverActor ! req

    case JayResponse(res) => println("Server response : " + res)
    case _ => println("get unrecognized message")
  }
}