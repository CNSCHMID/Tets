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

	Object info = null;
	ISession session = null;

	protected AbstractMessage(){
	}

	@Override
	public void setInfo(Object info) {
		this.info = info;
	}

	@Override
	public Object getInfo() {
		return null;
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
