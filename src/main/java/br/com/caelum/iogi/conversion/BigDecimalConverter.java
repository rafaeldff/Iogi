package br.com.caelum.iogi.conversion;

import java.math.BigDecimal;

import br.com.caelum.iogi.reflection.Target;

public class BigDecimalConverter extends TypeConverter<BigDecimal> {

	@Override
	protected BigDecimal convert(final String stringValue, final Target<?> to) {
		return new BigDecimal(stringValue);
	}

	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == BigDecimal.class;
	}


}
