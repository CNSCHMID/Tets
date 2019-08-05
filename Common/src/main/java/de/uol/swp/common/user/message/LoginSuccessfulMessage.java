package de.uol.swp.common.user.message;

import de.uol.swp.common.message.AbstractMessage;

/**
 * A message containing the session (typically for a new logged in user)
 *
 * @author Marco Grawunder
 *
 */
public class LoginSuccessfulMessage extends AbstractMessage {

	private static final long serialVersionUID = -9107206137706636541L;

	final String username;

	public LoginSuccessfulMessage(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

}
