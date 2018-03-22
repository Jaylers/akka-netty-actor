package com.td.akkasocket.myclient

import com.typesafe.scalalogging.StrictLogging
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.{HttpRequestDecoder, HttpResponseEncoder}
import io.netty.handler.codec.{DelimiterBasedFrameDecoder, Delimiters}

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

      val delimiters: Array[ByteBuf] = Array[ByteBuf](
        Unpooled.wrappedBuffer(Array[Byte]('\n')),
        Unpooled.wrappedBuffer(Array[Byte]('|')),
        Unpooled.wrappedBuffer(Array[Byte]('/'))) //Custom to separated the message instead of default \n
      bootstrap.handler(new ChannelInitializer[SocketChannel](){
        @throws[Exception]
        override def initChannel(channel: SocketChannel): Unit = {
          channel.pipeline()
            .addLast("decode", new HttpRequestDecoder())
            .addLast("encode", new HttpResponseEncoder())
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
