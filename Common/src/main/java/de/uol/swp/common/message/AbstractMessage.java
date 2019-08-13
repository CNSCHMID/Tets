package de.uol.swp.common.message;

import de.uol.swp.common.user.Session;

/**
 * Base class of all messages. Basic handling of session information
 *
 * @author Marco Grawunder
 *
 */
@SuppressWarnings("serial")
abstract public class AbstractMessage implements Message {

	MessageContext messageContext;
	Session session = null;

	@Override
	public MessageContext getMessageContext() {
		return messageContext;
	}

	@Override
	public void setMessageContext(MessageContext messageContext) {
		this.messageContext = messageContext;
	}

	@Override
	public void setSession(Session session){
		this.session = session;
	}

	@Override
	public Session getSession(){
		return session;
	}


}
