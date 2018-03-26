package com.td.akkasocket.myclient

import com.typesafe.scalalogging.StrictLogging
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.handler.codec.DelimiterBasedFrameDecoder

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

      //Custom to separated the message instead of default \n
      val delimiters: Array[ByteBuf] = Array[ByteBuf](Unpooled.wrappedBuffer(Array[Byte]('/')))
      bootstrap.handler(new ChannelInitializer[SocketChannel](){
        @throws[Exception]
        override def initChannel(channel: SocketChannel): Unit = {
          channel.pipeline()
            .addFirst(new DelimiterBasedFrameDecoder(4096, true, delimiters: _*))
            .addLast(new TcpClientHandler)
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
