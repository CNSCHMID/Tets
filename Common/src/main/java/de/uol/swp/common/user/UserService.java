package de.uol.swp.common.user;

import java.util.List;

/**
 * An interface for all methods of the user service
 *
 * @author Marco Grawunder
 *
 */

public interface UserService {

	/**
	 * Login with username and password
	 * @param username the name of the user
	 * @param password the password of the user
	 * @return a new user object
	 */
	User login(String username, String password);

	/**
	 * Login out from server
	 */
	void logout(User user);

	/**
	 * Retrieve the list of all current logged in users
	 * @return a list of users
	 */
	List<User> retrieveAllUsers();

}
