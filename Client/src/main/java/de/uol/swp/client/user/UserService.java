package de.uol.swp.client.user;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.*;
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

	@Inject
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
    public boolean isLoggedIn(User user) {
        throw new UnsupportedOperationException("Currently, not implemented");
    }

	@Override
	public void logout(User username){
		LogoutRequest msg = new LogoutRequest();
		bus.post(msg);
	}

	@Override
	public User createUser(User user) {
		RegisterUserRequest request = new RegisterUserRequest(user);
		bus.post(request);
		return null;
	}

    public void dropUser(User user) {
        //TODO: Implement me
    }

	@Override
	public User updateUser(User user) {
		UpdateUserRequest request = new UpdateUserRequest(user);
		bus.post(request);
		return null;
	}


	@Override
	public List<User> retrieveAllUsers() {
		RetrieveAllOnlineUsersRequest cmd = new RetrieveAllOnlineUsersRequest();
		bus.post(cmd);
		return null; // asynch call
	}




}
