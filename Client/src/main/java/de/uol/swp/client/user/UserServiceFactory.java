package de.uol.swp.client.user;

import de.uol.swp.common.user.UserService;
import io.netty.channel.Channel;

/**
 * A very simple way to hide the real communication from the application
 *
 * @author Marco Grawunder
 *
 */
public class UserServiceFactory {

	private static UserService userService;

	public static void init(Channel client){
		userService = new de.uol.swp.client.communication.object.UserService(client);
	}

	public static UserService getUserService() {
		return userService;
	}

}
