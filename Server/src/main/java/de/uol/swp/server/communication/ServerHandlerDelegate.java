package de.uol.swp.server.communication;

import de.uol.swp.common.message.RequestMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * An interface used to decouple ServerHandler and ServerApplication
 * 
 * @author Marco Grawunder
 *
 */

interface ServerHandlerDelegate {
	
	/**
	 * Is called when a new client connects
	 * 
	 * @param ctx The ChannelHandlerContext for this client
	 */
	void newClientConnected(ChannelHandlerContext ctx);

	/**
	 * Is called when a client disconnects
	 * 
	 * @param ctx The ChannelHandlerContext for this client
	 */
	void clientDisconnected(ChannelHandlerContext ctx);
	
	/**
	 * A message from a client connected via the ChannelHandlerContext ctx is received 
	 * and can be processed
	 * @param ctx The ChannelHandlerContext for this connection (identifies the client)
	 * @param msg The message send from the client
	 */
	void process(ChannelHandlerContext ctx, RequestMessage msg);

}
