package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * A request send from client to server to log out
 * 
 * @author Marco Grawunder
 *
 */

public class LogoutRequest extends AbstractRequestMessage{
	
	private static final long serialVersionUID = -5912075449879112061L;

	public LogoutRequest() {
		super();
	}

}
