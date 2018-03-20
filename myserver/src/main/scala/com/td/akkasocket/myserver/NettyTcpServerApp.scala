package com.td.akkasocket.myserver

import java.time.LocalDateTime

import akka.actor.ActorSystem
import com.td.akkasocket.myserver.actor.ProxyControllerActor
import com.typesafe.scalalogging.StrictLogging

import scala.io.StdIn

object NettyTcpServerApp extends StrictLogging {
  def main(args: Array[String]): Unit = {
    val time = LocalDateTime.now().toLocalDate.toString.substring(0, 10).concat(" ").concat(LocalDateTime.now().toLocalTime.toString.substring(0, 8))
    logger.info("******************************************************")
    logger.info("**           Welcome " + time + "            **")
    logger.info("******************************************************")
    val serverPort = 10500

    val system = ActorSystem("ProxyServer")
    system.actorOf(ProxyControllerActor.props(serverPort), "ProxyController")

    var msg = "Press 'End' to close the service"
    do {
      msg = StdIn.readLine()
    } while (msg.toLowerCase != "end")
    logger.info("Server is now Closes")
    system.terminate()
  }
}