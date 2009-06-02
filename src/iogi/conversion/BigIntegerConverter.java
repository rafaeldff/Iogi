package iogi.conversion;

import iogi.reflection.Target;

import java.math.BigInteger;

public class BigIntegerConverter extends TypeConverter<BigInteger> {
	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == BigInteger.class;
	}

	@Override
	protected BigInteger convert(final String stringValue, final Target<?> to) {
		return new BigInteger(stringValue);
	}
}
