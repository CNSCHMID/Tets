package exception;

/**
 * Exception to state e.g. that a login is required
 * @author Marco Grawunder
 *
 */
public class SecurityException extends RuntimeException {

	private static final long serialVersionUID = -6908340347082873591L;

	public SecurityException(String message){
		super(message);
	}
	
}
