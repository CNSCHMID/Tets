package de.uol.swp.client.user;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * This class is used to hide the communication details
 * implements IClientUserService
 *
 * @author Marco Grawunder
 *
 */

public class UserService implements de.uol.swp.common.user.UserService {

	private static final Logger LOG = LogManager.getLogger(UserService.class);
	private final EventBus bus;

	public UserService(EventBus bus) {
		this.bus = bus;
		// Currently not need, will only post on bus
		//bus.register(this);
	}

	@Override
	public User login(String username, String password){
		LoginRequest msg = new LoginRequest(username, password);
		bus.post(msg);
		return null; // asynch call
	}

	@Override
	public void logout(User username){
		LogoutRequest msg = new LogoutRequest();
		bus.post(msg);
	}


	@Override
	public List<User> retrieveAllUsers() {
		RetrieveAllOnlineUsersRequest cmd = new RetrieveAllOnlineUsersRequest();
		bus.post(cmd);
		return null; // asynch call
	}




}
