package de.uol.swp.common.message;

/**
 * Encapsulates an Exception in a message object
 * 
 * @author Marco Grawunder
 *
 */
public class ExceptionMessage extends AbstractResponseMessage{

	private static final long serialVersionUID = -7739395567707525535L;
	private final String exception;
	
	public ExceptionMessage(String message){
		this.exception = message;
	}
	
	public String getException() {
		return exception;
	}
	
}
