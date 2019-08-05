package de.uol.swp.common.user.command;

import de.uol.swp.common.message.AbstractMessage;

/**
 * A command send from client to server, trying to log in with
 * username and password
 * 
 * @author Marco Grawunder
 *
 */
public class LoginCommand extends AbstractMessage {

	private static final long serialVersionUID = 7793454958390539421L;
	String username;
	String password;
	
	public LoginCommand(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}

}
