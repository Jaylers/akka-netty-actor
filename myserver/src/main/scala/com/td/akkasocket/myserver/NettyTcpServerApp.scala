package com.td.akkasocket.myserver

import java.time.LocalDateTime

import akka.actor.ActorSystem
import com.td.akkasocket.myserver.actor.ProxyControllerActor

import scala.io.StdIn

object NettyTcpServerApp {
  def main(args: Array[String]): Unit = {
    val time = LocalDateTime.now().toLocalDate.toString.substring(0, 10).concat(" ").concat(LocalDateTime.now().toLocalTime.toString.substring(0, 8))
    println("******************************************************")
    println("**           Welcome " + time + "            **")
    println("******************************************************")
    val serverPort = 10500

    val system = ActorSystem("ProxyServer")
    system.actorOf(ProxyControllerActor.props(serverPort), "ProxyController")
    println("Server is now started")

    var msg = "Press 'End' to close the service"
    do {
      msg = StdIn.readLine()
    } while (msg.toLowerCase != "end")
    println("Server is now Closes")
    system.terminate()
  }
}