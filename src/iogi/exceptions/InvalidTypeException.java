package iogi.exceptions;


public class InvalidTypeException extends IogiException {
	public InvalidTypeException(String message, Object... args) {
		super(message, args);
	}

	private static final long serialVersionUID = 1L;

}
