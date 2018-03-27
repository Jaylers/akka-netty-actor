package com.td.akkasocket.myserver

import java.net.InetSocketAddress

import akka.actor.ActorRef
import com.typesafe.scalalogging.StrictLogging
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.handler.codec.{DelimiterBasedFrameDecoder, LengthFieldBasedFrameDecoder, LengthFieldPrepender}
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import io.netty.util.CharsetUtil

class ProxyServer(port:Int) extends StrictLogging {

  val bossGroup = new NioEventLoopGroup
  val workGroup = new NioEventLoopGroup

  def run (controllerRef: ActorRef): Unit = {
    try {
      val boot = new ServerBootstrap
      boot.group(bossGroup, workGroup)
        .channel(classOf[NioServerSocketChannel])
        .localAddress(new InetSocketAddress(port))
        .childHandler(new ChannelInitializer[SocketChannel](){
          override def initChannel(channel:SocketChannel):Unit={
            val delimiters: Array[ByteBuf] = Array[ByteBuf](Unpooled.wrappedBuffer(Array[Byte]('/')))
            channel.pipeline
              .addFirst("separate", new DelimiterBasedFrameDecoder(4096, true, delimiters: _*))
              .addLast("handler", new ProxyServerHandler(controllerRef))
          }
        }).option(ChannelOption.SO_BACKLOG, 128: java.lang.Integer)
        .childOption(ChannelOption.SO_KEEPALIVE, true: java.lang.Boolean)
      logger.info("Server is now started")
      val future = boot.bind().sync()
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
