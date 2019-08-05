package de.uol.swp.server.communication;

import de.uol.swp.common.message.IMessage;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

/**
 * Netty handler for incomming communication
 *
 * @author Marco Grawunder
 */
@Sharable
public class ServerHandler implements ChannelInboundHandler {

    private ServerHandlerDelegate delegate;

    /**
     * Creates a new ServerHandler
     *
     * @param delegate The ServerHandlerDelegate that should receive information about the connection
     */
    public ServerHandler(ServerHandlerDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        delegate.newClientConnected(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof IMessage) {
            delegate.process(ctx, (IMessage) msg);
        } else {
            System.err.println("Illegal Object read from channel. Ignored!");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive() || ctx.channel().isOpen()) {
            System.err.println("Exception caught " + cause);
        } else {
            delegate.clientDisconnected(ctx);
        }
    }

}
