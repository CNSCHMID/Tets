package de.uol.swp.server.communication;

import de.uol.swp.common.message.RequestMessage;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Netty handler for incomming communication
 *
 * @author Marco Grawunder
 */
@Sharable
class ServerHandler implements ChannelInboundHandler {

    private static final Logger LOG = LogManager.getLogger(ServerHandler.class);

    private final ServerHandlerDelegate delegate;

    /**
     * Creates a new ServerHandler
     *
     * @param delegate The ServerHandlerDelegate that should receive information about the connection
     */
    public ServerHandler(ServerHandlerDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext channelHandlerContext) {

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext channelHandlerContext) {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        delegate.newClientConnected(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Server ignores everything but IRequestMessages
        if (msg instanceof RequestMessage) {
            delegate.process(ctx, (RequestMessage) msg);
        } else {
            LOG.error("Illegal Object read from channel. Ignored!");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object o) {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext channelHandlerContext) {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (ctx.channel().isActive() || ctx.channel().isOpen()) {
            LOG.error("Exception caught " + cause);
        } else {
            delegate.clientDisconnected(ctx);
        }
    }

}
