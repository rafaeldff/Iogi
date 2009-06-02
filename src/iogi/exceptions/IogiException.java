package iogi.exceptions;

public class IogiException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IogiException(final String message, final Object... args) {
		super(String.format(message, args));
	}

	public IogiException(final Throwable cause) {
		super(cause);
	}

	public IogiException(final Throwable cause, final String message, final Object... args) {
		super(String.format(message, args), cause);
	}
}
