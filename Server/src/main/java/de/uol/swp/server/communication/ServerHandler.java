package de.uol.swp.server.communication;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import de.uol.swp.common.message.IMessage;

/**
 * Netty handler for incomming communication
 * 
 * @author Marco Grawunder
 *
 */
@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

	private ServerHandlerDelegate delegate;

	/**
	 * Creates a new ServerHandler
	 * @param delegate The ServerHandlerDelegate that should receive information about the connection 
	 */
	public ServerHandler(ServerHandlerDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		delegate.newClientConnected(ctx);
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
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (ctx.channel().isActive() || ctx.channel().isOpen()) {
			System.err.println("Exception caught " + cause);
		} else {
			delegate.clientDisconnected(ctx);
		}
	}

}
