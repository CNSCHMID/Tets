package user.message;

import message.AbstractMessage;
import user.Session;

/**
 * A message to indicate a logged out user
 * @author Marco Grawunder
 *
 */
public class UserLoggedOutMessage extends AbstractMessage{

	private static final long serialVersionUID = -2071886836547126480L;
	private String username;
	
	public UserLoggedOutMessage(){
		super(Session.invalid);
	}
	
	public UserLoggedOutMessage(String username){
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
}
