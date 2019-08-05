package de.uol.swp.client.communication.object;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import de.uol.swp.common.message.IMessage;

/**
 * Netty handler for incoming connections
 * 
 * @author Marco Grawunder
 *
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

	private Client client;

	public ClientHandler(Client client) {
		this.client = client;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Connected to server " + ctx);
		client.fireConnectionEstablished(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) throws Exception {
		if (in instanceof IMessage) {
			client.receivedMessage((IMessage) in);
		}else{
			System.err.println("Illegal Object read from channel. Ignored!");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		client.process(cause);
		ctx.close();
	}
}
