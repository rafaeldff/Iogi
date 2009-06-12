package iogi.util;

import iogi.exceptions.InvalidTypeException;
import iogi.reflection.Target;

import java.lang.reflect.ParameterizedType;

public class Assert {

	public static void isNotARawType(final Target<?> target) {
		if (!(target.getType() instanceof ParameterizedType))
			throw new InvalidTypeException("Expecting a parameterized list type, got raw type \"%s\" instead", target.getType());
	}

}
