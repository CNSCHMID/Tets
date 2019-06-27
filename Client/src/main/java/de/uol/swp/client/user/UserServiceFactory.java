package de.uol.swp.client.user;

import de.uol.swp.client.communication.object.ObjectCommunication;
import de.uol.swp.common.user.IUserService;
import io.netty.channel.Channel;

/**
 * A very simple way to hide the real communication from the application
 *
 * @author Marco Grawunder
 *
 */
public class UserServiceFactory {

	static IUserService userService;

	public static void init(Channel client){
		userService = new ObjectCommunication(client);
	}

	public static IUserService getUserService() {
		return userService;
	}

}
