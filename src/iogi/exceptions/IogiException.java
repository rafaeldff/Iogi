package iogi.exceptions;

public class IogiException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IogiException(String message, Object... args) {
		super(String.format(message, args));
	}

	public IogiException(Throwable cause) {
		super(cause);
	}
}
