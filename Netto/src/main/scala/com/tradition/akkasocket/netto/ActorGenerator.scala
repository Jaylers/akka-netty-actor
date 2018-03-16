package com.tradition.akkasocket.netto

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.tradition.akkasocket.shared.SharedMessage.{JayRequest, JayResponse}
import com.typesafe.config.ConfigFactory

import scala.util.Random

object ActorGenerator {
  def generate(): ActorRef = {
    val name = s"${Random.alphanumeric take 10 mkString}"
    val system = ActorSystem("server", ConfigFactory.load)
    system.actorOf(Props[ActorGenerator], name=name)
  }
}

class ActorGenerator extends Actor {
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