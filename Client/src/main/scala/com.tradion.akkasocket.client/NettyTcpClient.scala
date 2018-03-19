package com.tradion.akkasocket.client

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.StdIn

object NettyTcpClient {
  def main(args: Array[String]): Unit = {
    val port = new ClientConnector().run()
    println("Welcome" + port)
    port.foreach{
      port =>
        val client = new TcpClient(port)
        Future(client.run())
        println("Client is now, started")
        StdIn.readLine()

        client.close()
    }
  }
}
