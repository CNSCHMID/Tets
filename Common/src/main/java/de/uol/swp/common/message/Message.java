package de.uol.swp.common.message;

import de.uol.swp.common.user.Session;

import java.io.Serializable;


/**
 * Base interface of all messages
 * @author Marco Grawunder
 *
 */

public interface Message extends Serializable{

	void setMessageContext(MessageContext messageContext);
	MessageContext getMessageContext();

	void setSession(Session session);
	Session getSession();

	void initWithMessage(Message otherMessage);
}
