package iogi.exceptions;


public class InvalidTypeException extends IogiException {
	private static final long serialVersionUID = 1L;
	
	public InvalidTypeException(final String message, final Object... args) {
		super(message, args);
	}
	
	public InvalidTypeException(final Throwable cause, final String message, final Object... args) {
		super(cause, message, args);
	}
}
