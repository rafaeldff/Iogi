package iogi;

public class IogiException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IogiException() {
	}

	public IogiException(String message) {
		super(message);
	}

	public IogiException(Throwable cause) {
		super(cause);
	}

	public IogiException(String message, Throwable cause) {
		super(message, cause);
	}

}
