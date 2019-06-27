package exception;

import message.AbstractMessage;

/**
 * Encapsulates an Exception in a message object
 * 
 * @author Marco Grawunder
 *
 */
public class ExceptionMessage extends AbstractMessage {

	private static final long serialVersionUID = -7739395567707525535L;
	Exception exception;
	
	public ExceptionMessage(Exception exception){
		this.exception = exception;
	}
	
	public Exception getException() {
		return exception;
	}
	
}
