package iogi.conversion;

import java.math.BigInteger;

public class BigIntegerConverter extends TypeConverter<BigInteger> {
	@Override
	public BigInteger convert(String stringValue) {
		return new BigInteger(stringValue);
	}

	@Override
	public Class<BigInteger> targetClass() {
		return BigInteger.class;
	}
}
