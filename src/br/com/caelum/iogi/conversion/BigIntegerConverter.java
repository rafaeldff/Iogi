package br.com.caelum.iogi.conversion;

import java.math.BigInteger;

import br.com.caelum.iogi.reflection.Target;

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
