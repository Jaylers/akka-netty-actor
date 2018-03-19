package com.tradion.akkasocket.client

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.handler.logging.{LogLevel, LoggingHandler}

class TcpClient(){


  val bossGroup = new NioEventLoopGroup
  val workerGroup = new NioEventLoopGroup

  def run(): Unit = {

    try {
      val bootstrap = new ServerBootstrap
      bootstrap.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer[SocketChannel]() {
          override def initChannel(ch: SocketChannel): Unit = {
            ch.pipeline.addLast(new TcpClientHandler)
          }
        }).option(ChannelOption.SO_BACKLOG, 128: java.lang.Integer)
        .childOption(ChannelOption.SO_KEEPALIVE, true: java.lang.Boolean)

      // Bind and start to accept incoming connections.
      println("[TcpClient] New TCP connection")
      val future = bootstrap.bind().sync()
      future.channel().closeFuture().sync()

      // Wait until the server socket is closed.
      future.channel.closeFuture.sync
    } finally {
      close()
    }
  }

  def close(): Unit = {
    workerGroup.shutdownGracefully()
    bossGroup.shutdownGracefully()
  }

}
