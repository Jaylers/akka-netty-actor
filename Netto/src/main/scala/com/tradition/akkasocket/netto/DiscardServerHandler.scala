package com.tradition.akkasocket.netto

import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.channel._
import java.util.logging.Logger

class DiscardServerHandler extends SimpleChannelUpstreamHandler {

  val logger: Logger = Logger.getLogger(getClass.getName)

  var transferredBytes = 0L

  def getTransferredBytes: Long = transferredBytes

  override def handleUpstream(ctx: ChannelHandlerContext, e: ChannelEvent) {
    e match {
      case event: ChannelStateEvent=> {
        event.getState match {
          case ChannelState.OPEN => println("ChannelState is now open")
          case ChannelState.BOUND => println("ChannelState is now bound")
          case ChannelState.CONNECTED => {
            //Build Actor and send back
          }
        }
      }
      case _                    => println("get none")
    }
    super.handleUpstream(ctx, e)
  }

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
    // Discard received data silently by doing nothing.
    transferredBytes += ((e.getMessage match {
      case c: ChannelBuffer => c
      case _                => throw new ClassCastException
    }) readableBytes)
  }

  override def exceptionCaught(context: ChannelHandlerContext, e: ExceptionEvent) {
    // Close the connection when an exception is raised.
    logger.warning("Unexpected exception from downstream." + e.getCause)
    e.getChannel.close()
  }

}