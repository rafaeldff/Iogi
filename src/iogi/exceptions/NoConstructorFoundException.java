package iogi.exceptions;


public class NoConstructorFoundException extends IogiException {
	private static final long serialVersionUID = 1L;

	public NoConstructorFoundException(String message, Object... arguments) {
		super(message, arguments);
	}
}
