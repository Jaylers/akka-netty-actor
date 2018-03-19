package com.tradition.akkasocket.server

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

object NettyTcpServer {

  def main(args: Array[String]): Unit = {
    val server = new DiscardServer()
    Future(server.run())

    println("Server is now started")
    StdIn.readLine()

    //close is a def of DiscardServer's method
    server.close()
  }
}
