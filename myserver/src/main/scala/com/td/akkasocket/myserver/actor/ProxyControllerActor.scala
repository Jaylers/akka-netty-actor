package com.td.akkasocket.myserver.actor

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import com.td.akkasocket.myserver.ProxyServer
import com.td.akkasocket.myserver.actor.ProxyControllerActor.{CreateClient, Heartbeat, Kill, RandomKill}
import io.netty.channel.ChannelHandlerContext

import scala.concurrent.Future
import scala.util.Random

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ProxyControllerActor {
  def props(serverPort: Int): Props = Props(new ProxyControllerActor(serverPort))

  case class CreateClient(ctx: ChannelHandlerContext)

  case object Heartbeat

  case object Kill

  case object RandomKill

}

class ProxyControllerActor(serverPort: Int) extends Actor with Timers with ActorLogging {
  // client actor collection
  var clients: Map[UUID, ActorRef] = Map()

  // create TCP connection
  val server = new ProxyServer(serverPort)

  Future(server.run(self))

  timers.startPeriodicTimer("heartbeat", Heartbeat, 2.seconds)
  timers.startPeriodicTimer("killer", RandomKill, 20.seconds)

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
        val index = Random.nextInt(clients.size)
        val (clientId, clientRef) = clients.toIndexedSeq.toList(index)
        log.info(s"Random kill ($index)")

        clientRef ! Kill
        clients = clients - clientId
      }
  }

  override def unhandled(message: Any): Unit = {
    log.error(s"Receive unhandled message: $message")
  }

  override def postStop(): Unit = {
    server.close()
  }
}
