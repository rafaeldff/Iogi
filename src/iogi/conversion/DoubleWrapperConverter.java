package iogi.conversion;

import iogi.reflection.Target;

public class DoubleWrapperConverter extends TypeConverter<Double> {
	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == Double.class;
	}

	@Override
	protected Double convert(final String stringValue, final Target<?> to) {
		return Double.valueOf(stringValue);
	}
}
