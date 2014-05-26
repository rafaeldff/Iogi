package br.com.caelum.iogi.exceptions;


public class ConversionException extends InvalidTypeException {
	private static final long serialVersionUID = 1L;

	public ConversionException(final String message, final Object... args) {
		super(message, args);
	}
	
	public ConversionException(final Throwable cause, final String message, final Object... args) {
		super(cause, message, args);
	}
}
