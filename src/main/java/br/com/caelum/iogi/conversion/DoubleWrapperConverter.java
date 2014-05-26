package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.reflection.Target;


public class DoubleWrapperConverter extends TypeConverter<Double> {
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == Double.class;
	}

	@Override
	protected Double convert(final String stringValue, final Target<?> to) {
		return Double.valueOf(stringValue);
	}
}
