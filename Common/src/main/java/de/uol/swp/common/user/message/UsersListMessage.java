package de.uol.swp.common.user.message;

import java.util.ArrayList;
import java.util.List;

import de.uol.swp.common.message.AbstractServerMessage;

/**
 * A message containing all current logged in usernames
 * 
 * @author Marco Grawunder
 *
 */
public class UsersListMessage extends AbstractServerMessage {

	private static final long serialVersionUID = -7968574381977330152L;
	private ArrayList<String> users;
	
	public UsersListMessage(List<String> users){
		this.users = new ArrayList<>(users);
	}
	
	public ArrayList<String> getUsers() {
		return users;
	}
	
}
