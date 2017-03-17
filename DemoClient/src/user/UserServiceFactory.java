package user;

import communication.object.ObjectCommunication;
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
