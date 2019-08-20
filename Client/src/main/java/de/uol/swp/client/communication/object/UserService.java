package de.uol.swp.client.communication.object;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import io.netty.channel.Channel;

import java.io.Serializable;
import java.util.List;

/**
 * This class is used to hide the communication details
 * implements IClientUserService
 *
 * @author Marco Grawunder
 *
 */

public class UserService implements de.uol.swp.common.user.UserService {


	/**
	 * The physical connection to the client
	 */
	private final Channel client;

	/**
	 * Creates a new Communication object with the connection information
	 * @param client
	 */
	public UserService(Channel client) {
		this.client = client;
	}

	@Override
	public User login(String username, String password){
		LoginRequest msg = new LoginRequest(username, password);
		sendMessage(msg);
		return null; // asynch call
	}

	@Override
	public void logout(User username){
		LogoutRequest msg = new LogoutRequest();
		sendMessage(msg);
	}


	@Override
	public List<User> retrieveAllUsers() {
		RetrieveAllOnlineUsersRequest cmd = new RetrieveAllOnlineUsersRequest();
		sendMessage(cmd);
		return null; // asynch call
	}

	private void sendMessage(Serializable msg) {
		client.writeAndFlush(msg);
	}

}
