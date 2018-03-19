package com.tradion.akkasocket.client

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.tradition.akkasocket.shared.SharedMessage._
import com.typesafe.config.ConfigFactory

object ActorClient {
  var path = "akka.tcp://ServerSystem@127.0.0.1:";
  def generate(port:Int)={
    path = path.concat(port.toString).concat("/user/server")
    println("Connecting to server path : " + path)
    val system = ActorSystem("ClientSystem", ConfigFactory.load)
    val clientActor = system.actorOf(Props[ActorClient], name="client")
    var msg = "Client is ready on : " + clientActor.path.address
    do {
      msg match {
        case _ => clientActor ! JayRequest(msg)
      }
      msg = scala.io.StdIn.readLine()
    } while (msg.toUpperCase() != "END")
    system.terminate()
  }

  class ActorClient extends Actor {
    val serverActor: ActorSelection = context.actorSelection(path)

    override def receive: Receive = {
      case req: JayRequest => serverActor ! req
      case req: CodeRequest => serverActor ! req
      case req: ServerRequest => serverActor ! ServerResponse(req.s)

      case JayResponse(res) => println("Server response : " + res)
      case _ => println("get unrecognized message")
    }
  }
}
