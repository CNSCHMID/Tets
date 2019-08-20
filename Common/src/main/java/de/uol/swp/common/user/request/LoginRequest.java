package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * A request send from client to server, trying to log in with
 * username and password
 * 
 * @author Marco Grawunder
 *
 */
public class LoginRequest extends AbstractRequestMessage {

	private static final long serialVersionUID = 7793454958390539421L;
	private String username;
	private String password;
	
	public LoginRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public boolean authorizationNeeded() {
		return false;
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
