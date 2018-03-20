package com.td.akkasocket.myclient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

object NettyTcpClientApp {
  def main(args: Array[String]): Unit = {
    val client = new TcpClient(10500)
    Future(client.run())
    println("Server is now started")

    var msg = "Hello"
    do {
      msg = StdIn.readLine()
    } while (msg.toLowerCase() != "end")
    println("Server is now stopped")
    client.close()
  }
}
