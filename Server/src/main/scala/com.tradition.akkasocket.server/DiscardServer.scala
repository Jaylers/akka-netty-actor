package com.tradition.akkasocket.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel

class DiscardServer {
  val bossGroup = new NioEventLoopGroup
  val workerGroup = new NioEventLoopGroup

  import io.netty.channel.socket.nio.NioServerSocketChannel
  import io.netty.channel.{ChannelInitializer, ChannelOption}

  @throws[Exception]
  def run(): Unit = {
    try {
      val bootstrap = new ServerBootstrap()
      bootstrap.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .childHandler( new ChannelInitializer[SocketChannel] {
          @throws[Exception]
          override def initChannel(ch: SocketChannel): Unit = {
            ch.pipeline.addLast(new DiscardServerHandler())
          }
        }).option(ChannelOption.SO_BACKLOG, 128: java.lang.Integer)
        .childOption(ChannelOption.SO_KEEPALIVE, true: java.lang.Boolean)

      val future = bootstrap.bind(9000).sync
      future.channel.closeFuture.sync
    } finally {
      workerGroup.shutdownGracefully()
      bossGroup.shutdownGracefully()
    }
  }

  def close(): Unit ={
    workerGroup.shutdownGracefully()
    bossGroup.shutdownGracefully()
  }

  def ServerGenerator():Unit={
    try {
      val bt = new ServerBootstrap()
      bt.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .childHandler( new ChannelInitializer[SocketChannel] {
          @throws[Exception]
          override def initChannel(ch: SocketChannel): Unit = {
            ch.pipeline.addLast(new DiscardServerHandler())
          }
        })
        .option(ChannelOption.SO_BACKLOG, 128: java.lang.Integer)
        .childOption(ChannelOption.SO_KEEPALIVE, true: java.lang.Boolean)

      val ft = bt.bind().sync
      println("New server starting")
      ft.channel.closeFuture.sync
    } finally {
      workerGroup.shutdownGracefully()
      bossGroup.shutdownGracefully()
    }
  }
}