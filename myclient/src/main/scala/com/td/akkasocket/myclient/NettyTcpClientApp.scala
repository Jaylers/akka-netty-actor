package com.td.akkasocket.myclient

import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

object NettyTcpClientApp extends StrictLogging {
  def main(args: Array[String]): Unit = {
    val client = new TcpClient(10500)
    Future(client.run())
    logger.info("Service is now started")

    var msg = ""
    do {
      logger.info("Press 'end' to stop service")
      msg = StdIn.readLine()
    } while (msg.toLowerCase() != "end")
    logger.info("Service is now stopped")
    client.close()
  }
}
