package message;

import java.io.Serializable;

import user.Session;

/**
 * Base interface of all messages
 * @author Marco Grawunder
 *
 */

public interface IMessage extends Serializable{
	/**
	 * Delivers the session for this message (could be invalid)
	 * @return
	 */
	Session getSession();

	/**
	 * Is the session for this message valid?
	 * @return
	 */
	boolean hasValidSession();

	/**
	 * Throws an exception if the message should only be treated with a valid session
	 */
	void forceSession();

	/**
	 *
	 */
	void setInfo(Object object);

	/**
	 *
	 */
	Object getInfo();

}
