package com.td.akkasocket.myclient

import com.typesafe.scalalogging.StrictLogging
import io.netty.bootstrap.Bootstrap
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

class TcpClient(port: Int) extends StrictLogging {
  val bossGroup = new NioEventLoopGroup

  def run(): Unit = {
    val host = "localhost"
    val port = 10500

    try {
      val bootstrap = new Bootstrap()
      bootstrap.group(bossGroup)
      bootstrap.channel(classOf[NioSocketChannel])
      bootstrap.option(ChannelOption.SO_KEEPALIVE, true: java.lang.Boolean)
      bootstrap.handler(new ChannelInitializer[SocketChannel](){
        @throws[Exception]
        override def initChannel(ch: SocketChannel): Unit = {
          ch.pipeline().addLast(new TcpClientHandler)
        }
      })
      logger.info("Connecting server")

      val future = bootstrap.connect(host, port).sync()
      future.channel().closeFuture().sync()
    } finally {
      bossGroup.shutdownGracefully()
    }
  }

  def close(): Unit = {
    bossGroup.shutdownGracefully()
  }

}
