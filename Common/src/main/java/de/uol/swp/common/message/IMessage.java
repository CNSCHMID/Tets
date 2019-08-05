package de.uol.swp.common.message;

import de.uol.swp.common.user.ISession;

import java.io.Serializable;


/**
 * Base interface of all messages
 * @author Marco Grawunder
 *
 */

public interface IMessage extends Serializable{

	void setInfo(Object info);
	Object getInfo();

	void setSession(ISession session);
	ISession getSession();

}
