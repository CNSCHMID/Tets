package de.uol.swp.common.message;

/**
 * Encapsulates an Exception in a message object
 * 
 * @author Marco Grawunder
 *
 */
public class ExceptionMessage extends AbstractResponseMessage{

	private static final long serialVersionUID = -7739395567707525535L;
	Exception exception;
	
	public ExceptionMessage(Exception exception){
		this.exception = exception;
	}
	
	public Exception getException() {
		return exception;
	}
	
}
