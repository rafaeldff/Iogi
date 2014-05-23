package br.com.caelum.iogi.exceptions;


public class NoConstructorFoundException extends IogiException {
	private static final long serialVersionUID = 1L;

	public NoConstructorFoundException(final String message, final Object... arguments) {
		super(message, arguments);
	}
}
