package com.td.akkasocket.myclient

import com.typesafe.scalalogging.StrictLogging
import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import io.netty.handler.codec.{LengthFieldBasedFrameDecoder, LengthFieldPrepender}
import io.netty.util.CharsetUtil

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
        override def initChannel(channel: SocketChannel): Unit = {
          channel.pipeline
            //This will know the header of each message and can get it one by one of message
            .addFirst(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
            .addLast("frameEncoder", new LengthFieldPrepender(4))
            .addLast("decoder", new StringDecoder(CharsetUtil.UTF_8))
            .addLast("encoder", new StringEncoder(CharsetUtil.UTF_8))
            .addLast("handler", new TcpClientHandler)
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
