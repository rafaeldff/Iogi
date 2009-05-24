package iogi.exceptions;


public class InvalidTypeException extends IogiException {
	private static final long serialVersionUID = 1L;
	
	public InvalidTypeException(String message, Object... args) {
		super(message, args);
	}
	
	public InvalidTypeException(Throwable cause, String message, Object... args) {
		super(cause, message, args);
	}
}
