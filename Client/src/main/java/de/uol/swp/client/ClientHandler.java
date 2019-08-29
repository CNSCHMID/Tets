package de.uol.swp.client;

import de.uol.swp.common.message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Netty handler for incoming connections
 * 
 * @author Marco Grawunder
 *
 */
class ClientHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOG = LogManager.getLogger(ClientHandler.class);

	private final Client client;

	ClientHandler(Client client) {
		this.client = client;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		LOG.debug("Connected to server " + ctx);
		client.fireConnectionEstablished(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) {
		if (in instanceof Message) {
			client.receivedMessage((Message) in);
		}else{
			LOG.error("Illegal Object read from channel. Ignored!");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		LOG.error(cause);
		client.process(cause);
		ctx.close();
	}
}
