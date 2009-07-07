package br.com.caelum.iogi.util;

import java.lang.reflect.ParameterizedType;

import br.com.caelum.iogi.exceptions.InvalidTypeException;
import br.com.caelum.iogi.reflection.Target;

public class Assert {

	public static void isNotARawType(final Target<?> target) {
		if (!(target.getType() instanceof ParameterizedType))
			throw new InvalidTypeException("Expecting a parameterized list type, got raw type \"%s\" instead", target.getType());
	}

}
