package com.tradion.akkasocket.client

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.StdIn

object NettyTcpClient {
  def main(args: Array[String]): Unit = {
    new ClientConnector().run()
    println("Welcome")
    val client = new TcpClient()
    Future(client.run())
    println("Client is now, started")
    StdIn.readLine()

    client.close()
  }
}
