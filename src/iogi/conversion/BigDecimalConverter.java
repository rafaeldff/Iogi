package iogi.conversion;

import iogi.reflection.Target;

import java.math.BigDecimal;

public class BigDecimalConverter extends TypeConverter<BigDecimal> {

	@Override
	protected BigDecimal convert(String stringValue, Target<?> to) {
		return new BigDecimal(stringValue);
	}

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == BigDecimal.class;
	}


}
