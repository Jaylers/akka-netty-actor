package com.tradion.akkasocket.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.{ChannelFuture, ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

class ClientConnector {
  var port: Option[Int] = None

  def run(): Option[Int] = {
    val serverHost = "localhost"
    val serverPort = 9000
    val workerGroup = new NioEventLoopGroup

    try {
      val bootstrap = new Bootstrap
      bootstrap.group(workerGroup)
      bootstrap.channel(classOf[NioSocketChannel])
      bootstrap.option(ChannelOption.SO_KEEPALIVE, true: java.lang.Boolean)
      bootstrap.handler(new ChannelInitializer[SocketChannel]() {
        @throws[Exception]
        override def initChannel(ch: SocketChannel): Unit = {
          ch.pipeline.addLast(new ClientConnectorHandler(clientPortCallback))
        }
      })
      // Start the client.
      val future: ChannelFuture = bootstrap.connect(serverHost, serverPort).sync
      // Wait until the connection is closed.
      future.channel.closeFuture.sync
    } finally {
      workerGroup.shutdownGracefully()
      None
    }
    println("[ClientConnector] Server return port : " + Some(port))
    port
  }

  def clientPortCallback(newPort: Int): Unit = {
    println(s"[ClientConnector] Get new port : $newPort")
    // use some because if server did not send the port or connection error
    port = Some(newPort)
  }
}
