package de.uol.swp.client.demo;

import io.netty.channel.Channel;

public interface IConnectionListener {

	/**
	 * Is called when the connection to the server has been established
	 * @param channel
	 */
	void connectionEstablished(Channel channel);
	
	/**
	 * If the server sends an exception, this method is called
	 * @param cause
	 */
	void exceptionOccured(Throwable cause);
	
}
