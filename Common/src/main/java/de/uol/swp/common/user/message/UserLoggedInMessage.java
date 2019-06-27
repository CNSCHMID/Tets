package de.uol.swp.common.user.message;

import de.uol.swp.common.message.AbstractMessage;
import de.uol.swp.common.user.Session;

/**
 * A message to indicate a new logged in user
 * @author Marco Grawunder
 *
 */
public class UserLoggedInMessage extends AbstractMessage {

	private static final long serialVersionUID = -2071886836547126480L;
	private String username;
	
	public UserLoggedInMessage(){
		super(Session.invalid);
	}
	
	public UserLoggedInMessage(String username){
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
}
