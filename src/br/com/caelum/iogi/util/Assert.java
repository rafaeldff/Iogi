package br.com.caelum.iogi.util;

import br.com.caelum.iogi.exceptions.InvalidTypeException;
import br.com.caelum.iogi.reflection.Target;

public class Assert {

	public static void isNotARawType(final Target<?> target) {
		if (!target.isParameterized())
			throw new InvalidTypeException("Expecting a parameterized list type, got raw type \"%s\" instead", target.getType());
	}

}
