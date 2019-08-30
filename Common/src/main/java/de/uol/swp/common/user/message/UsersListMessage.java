package de.uol.swp.common.user.message;

import de.uol.swp.common.message.AbstractServerMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * A message containing all current logged in usernames
 * 
 * @author Marco Grawunder
 *
 */
public class UsersListMessage extends AbstractServerMessage {

	private static final long serialVersionUID = -7968574381977330152L;
	private final ArrayList<String> users;
	
	public UsersListMessage(List<String> users){
		this.users = new ArrayList<>(users);
	}
	
	public ArrayList<String> getUsers() {
		return users;
	}
	
}
