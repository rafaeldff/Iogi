package iogi.conversion;

import iogi.exceptions.InvalidTypeException;

public class ConversionException extends InvalidTypeException {
	private static final long serialVersionUID = 1L;

	public ConversionException(String message, Object... args) {
		super(message, args);
	}
	
	public ConversionException(Throwable cause, String message, Object... args) {
		super(cause, message, args);
	}
}
