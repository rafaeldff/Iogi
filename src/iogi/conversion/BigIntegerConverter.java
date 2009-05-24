package iogi.conversion;

import iogi.reflection.Target;

import java.math.BigInteger;

public class BigIntegerConverter extends TypeConverter<BigInteger> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == BigInteger.class;
	}

	@Override
	public BigInteger convert(String stringValue) {
		return new BigInteger(stringValue);
	}
}
