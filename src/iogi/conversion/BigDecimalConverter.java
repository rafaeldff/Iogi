package iogi.conversion;

import iogi.reflection.Target;

import java.math.BigDecimal;

public class BigDecimalConverter extends TypeConverter<BigDecimal> {

	@Override
	public BigDecimal convert(String stringValue) {
		return new BigDecimal(stringValue);
	}

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == BigDecimal.class;
	}


}
