package de.uol.swp.common.user;

import java.util.List;

/**
 * An interface for all methods of the user service
 *
 * @author Marco Grawunder
 *
 */

public interface IUserService {

	/**
	 * Login with username and password
	 * @param username the name of the user
	 * @param password the password of the user
	 * @return a session object
	 */
	Session login(String username, String password);

	/**
	 * Login out from server
	 */
	String logout(Session session);

	/**
	 * Retrieve the list of all current logged in users
	 * @param session The current logged in users are only visible if a user is logged in
	 * @return a list of user names
	 */
	List<String> retrieveAllUsers(Session session);

}
