package com.td.akkasocket.myserver

import akka.actor.ActorSystem
import com.td.akkasocket.myserver.actor.ProxyControllerActor
import com.typesafe.scalalogging.StrictLogging

import scala.io.StdIn

object NettyTcpServerApp extends StrictLogging {
  def main(args: Array[String]): Unit = {
    logger.info("Starting server . .")

    val system = ActorSystem("ProxyServer")
    system.actorOf(ProxyControllerActor.props(10500), "ProxyController")
    logger.info("Any key to stop service")
    StdIn.readLine()
    logger.info("Server is now Closes")
    system.terminate()
  }
}