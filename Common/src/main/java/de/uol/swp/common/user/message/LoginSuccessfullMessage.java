package de.uol.swp.common.user.message;

import de.uol.swp.common.message.AbstractMessage;
import de.uol.swp.common.user.Session;

/**
 * A message containing the session (typically for a new logged in user)
 *
 * @author Marco Grawunder
 *
 */
public class LoginSuccessfullMessage extends AbstractMessage {

	private static final long serialVersionUID = -9107206137706636541L;

	final String username;

	public LoginSuccessfullMessage(Session session, String username) {
		super(session);
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

}
