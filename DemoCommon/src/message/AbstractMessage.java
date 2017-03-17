package message;

import user.Session;

/**
 * Base class of all messages. Basic handling of session information
 *
 * @author Marco Grawunder
 *
 */
@SuppressWarnings("serial")
abstract public class AbstractMessage implements IMessage{

	Session session = Session.invalid;
	Object info = null;

	protected AbstractMessage(){
	}

	protected AbstractMessage(Session session){
		this.session = session;
	}

	@Override
	public boolean hasValidSession() {
		return session != null && session.isValid();
	}

	@Override
	public void forceSession() {
		if (!hasValidSession()){
			throw new SecurityException("No valid session! User not logged in.");
		}
	}

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void setInfo(Object object) {
		this.info = object;
	}

	@Override
	public Object getInfo() {
		return info;
	}

}
