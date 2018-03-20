package com.td.akkasocket.myserver.actor

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import com.td.akkasocket.myserver.ProxyServer
import com.td.akkasocket.myserver.actor.ProxyControllerActor.CreateClient
import com.tradition.akkasocket.shared.Code.{Heartbeat, Kill, RandomKill}
import io.netty.channel.ChannelHandlerContext

import scala.concurrent.Future
import scala.util.Random
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ProxyControllerActor {
  def props(serverPort: Int): Props = Props(new ProxyControllerActor(serverPort))
  case class CreateClient(ctx: ChannelHandlerContext)
}

class ProxyControllerActor(serverPort: Int) extends Actor with Timers with ActorLogging {
  // client actor collection
  var clients: Map[UUID, ActorRef] = Map()

  // create TCP connection
  val server = new ProxyServer(serverPort)

  Future(server.run(self))

  timers.startPeriodicTimer("heartbeat", Heartbeat, 5.seconds) //Heartbeat every 5 sec.
  timers.startPeriodicTimer("killer", RandomKill, 20.seconds) //Killer will random to kill every 20 sec.

  def receive: Receive = {
    case CreateClient(ctx) =>
      log.info("create new client")
      // create client actor
      val client: ActorRef = context.actorOf(ClientConnectorActor.props(ctx))
      clients = clients + (UUID.randomUUID() -> client)

    case Heartbeat =>
      clients.foreach { case (_, clientRef) =>
        log.info("sending heartbeat")
        clientRef ! Heartbeat
      }

    case RandomKill =>
      if (clients.nonEmpty) {
        if (Random.nextInt(10) == 0){ //10% chance to kill the client
          val index = Random.nextInt(clients.size)
          val (clientId, clientRef) = clients.toIndexedSeq.toList(index)
          log.info(s"Random 10% : index ($index) was killed")
          clientRef ! Kill
          clients = clients - clientId
        } else {
          log.info("So lucky, No one get kill")
        }
      }
  }

  override def unhandled(message: Any): Unit = {
    log.error(s"Receive unhandled message: $message")
  }

  override def postStop(): Unit = {
    server.close()
  }
}
