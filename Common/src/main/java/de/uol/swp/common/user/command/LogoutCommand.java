package de.uol.swp.common.user.command;

import de.uol.swp.common.message.AbstractMessage;
import de.uol.swp.common.user.Session;

/**
 * A command send from client to server to log out
 * 
 * @author Marco Grawunder
 *
 */

public class LogoutCommand extends AbstractMessage {
	
	private static final long serialVersionUID = -5912075449879112061L;

	public LogoutCommand() {
		super();
	}

}
