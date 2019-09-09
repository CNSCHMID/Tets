package de.uol.swp.common.message;

import de.uol.swp.common.user.Session;

import java.util.Optional;

/**
 * Base class of all messages. Basic handling of session information
 *
 * @author Marco Grawunder
 *
 */
@SuppressWarnings("serial")
abstract public class AbstractMessage implements Message {

	private MessageContext messageContext;
	private Session session = null;

	@Override
	public Optional<MessageContext> getMessageContext() {
		return messageContext!=null? Optional.of(messageContext):Optional.empty();
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
	public Optional<Session> getSession(){
		return session!=null?Optional.of(session):Optional.empty();
	}

	@Override
	public void initWithMessage(Message otherMessage) {
		otherMessage.getMessageContext().ifPresent(this::setMessageContext);
		otherMessage.getSession().ifPresent(this::setSession);
	}
}
