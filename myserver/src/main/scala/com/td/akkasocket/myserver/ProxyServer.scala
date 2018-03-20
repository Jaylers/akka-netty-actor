package com.td.akkasocket.myserver

import akka.actor.ActorRef
import com.typesafe.scalalogging.StrictLogging
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelInitializer, ChannelOption}

class ProxyServer(port:Int) extends StrictLogging {

  val bossGroup = new NioEventLoopGroup
  val workGroup = new NioEventLoopGroup


  def run (controllerRef: ActorRef): Unit = {
    try {
      val boot = new ServerBootstrap
      boot.group(bossGroup, workGroup)
        .channel(classOf[NioServerSocketChannel])
        .childHandler(new ChannelInitializer[SocketChannel](){
          override def initChannel(ch:SocketChannel):Unit={
            ch.pipeline.addLast(new ProxyServerHandler(controllerRef))
          }
        }).option(ChannelOption.SO_BACKLOG, 128: java.lang.Integer)
        .childOption(ChannelOption.SO_KEEPALIVE, true: java.lang.Boolean)
      logger.info("Server is now started")
      val future = boot.bind(port).sync()
      future.channel.closeFuture.sync()
    } finally {
      close()
    }
  }

  def close(): Unit = { //Separate it from [finally] cause It's will easy to handel from outside
    bossGroup.shutdownGracefully()
    workGroup.shutdownGracefully()
  }
}
