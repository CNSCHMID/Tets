package de.uol.swp.server.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uol.swp.common.exception.SecurityException;
import de.uol.swp.common.user.IUserService;
import de.uol.swp.common.user.Session;

/**
 * Class encapsulates all user specific server services
 * 
 * @author Marco Grawunder
 *
 */
public class UserService implements IUserService {

	/*
	 * The should be only one instance of the user service (Singleton)
	 */
	static final UserService instance = new UserService();

	public static UserService getInstance() {
		return instance;
	}

	private UserService() {
	}

	/**
	 * The list of current logged in users
	 */
	final private List<String> users = new ArrayList<>();
	final private Map<Session, String> userSessions = new HashMap<>();

	@Override
	public Session login(String username, String password) {
		Session newSession;
		if (isValidLogin(username, password)) {
			newSession = new Session();
			this.users.add(username);
			this.userSessions.put(newSession, username);
			System.out.println("Logging in user with Session " + newSession);
		} else {
			newSession = Session.invalid;
		}
		return newSession;
	}

	public String logout(Session session) {
		if (session.isValid()) {
			String user = userSessions.get(session);
			users.remove(user);
			return user;
		}
		return null;
	}

	private boolean isValidLogin(String username, String password) {
		// TODO: Call real logic
		return username.equalsIgnoreCase(password);
	}

	@Override
	public List<String> retrieveAllUsers(Session session) {
		if (session.isValid()) {
			return Collections.unmodifiableList(users);
		}
		throw new SecurityException("Login Required!");
	}

}
