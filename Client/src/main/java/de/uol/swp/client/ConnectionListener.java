package de.uol.swp.client;

import io.netty.channel.Channel;

public interface ConnectionListener {

	/**
	 * Is called when the connection to the server has been established
	 * @param channel
	 */
	void connectionEstablished(Channel channel);
	
	/**
	 * If the server sends an exception, this method is called
	 * @param cause
	 */
	void exceptionOccured(String cause);
	
}
