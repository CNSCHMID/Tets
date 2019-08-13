package de.uol.swp.common.message;

import de.uol.swp.common.user.ISession;

/**
 * Base class of all messages. Basic handling of session information
 *
 * @author Marco Grawunder
 *
 */
@SuppressWarnings("serial")
abstract public class AbstractMessage implements IMessage{

	IMessageContext messageContext;
	ISession session = null;

	@Override
	public IMessageContext getMessageContext() {
		return messageContext;
	}

	@Override
	public void setMessageContext(IMessageContext messageContext) {
		this.messageContext = messageContext;
	}

	@Override
	public void setSession(ISession session){
		this.session = session;
	}

	@Override
	public ISession getSession(){
		return session;
	}


}
