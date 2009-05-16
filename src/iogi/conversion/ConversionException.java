package iogi.conversion;

import iogi.exceptions.IogiException;

public class ConversionException extends IogiException {
	private static final long serialVersionUID = 1L;

	public ConversionException(String message, Object... args) {
		super(message, args);
	}

	public ConversionException(Throwable cause) {
		super(cause);
	}

}
