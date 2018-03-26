package com.td.akkasocket.myserver.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated, Timers}
import com.td.akkasocket.myserver.ProxyServer
import com.td.akkasocket.myserver.actor.ProxyControllerActor.CreateClient
import com.tradition.akkasocket.shared.Code.{Heartbeat, Kill, RandomKill}
import io.netty.channel.ChannelHandlerContext

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

object ProxyControllerActor {
  def props(port: Int): Props = Props(new ProxyControllerActor(port))
  case class CreateClient(ctx: ChannelHandlerContext)
  case class RemoveClient(ctx: ChannelHandlerContext)
}

class ProxyControllerActor(port:Int) extends Actor with Timers with ActorLogging {
  // client actor collection
  var clients: Set[ActorRef] = Set()

  // create TCP connection
  val server = new ProxyServer(port)

  Future(server.run(self))

  timers.startPeriodicTimer("heartbeat", Heartbeat, 5.seconds) //Heartbeat every 5 sec.
  timers.startPeriodicTimer("killer", RandomKill, 20.seconds)  //Killer will random to kill every 20 sec.

  def receive: Receive = {
    case CreateClient(ctx) =>
      log.info("create new client")
      // create client actor
      val client: ActorRef = context.actorOf(ClientConnectorActor.props(ctx))
      context.watch(client)
      clients = clients + client
      clients.foreach { clientRef =>
        clientRef ! clients.size
      }

    case Heartbeat =>
      clients.foreach { clientRef =>
        log.info(">> sending "+ clients.size +" Heartbeat")
        clientRef ! Heartbeat
      }

    case RandomKill => //do every 20 sec see how to on line 29
      if (clients.nonEmpty) {
        if (Random.nextInt(10) == 0){ //10% chance to kill the client
          val index = Random.nextInt(clients.size)
          val clientRef = clients.toIndexedSeq.toList(index)
          log.info(s"[10% Random] : ($clientRef) was killed")
          clientRef ! Kill
        } else { log.info("So lucky, No one get kill") }
      } else { log.info("Server is running ... ") }

    case Terminated(actorRef) =>
      log.info("Remove the disconnect client's actor")
      clients = clients - actorRef
      clients.foreach { clientRef =>
        clientRef ! clients.size
      }
  }

  override def unhandled(message: Any): Unit = {
    log.error("Receive unhandled message: " + message)
  }

  override def postStop(): Unit = {
    server.close()
  }
}