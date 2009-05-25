package iogi.conversion;

import iogi.reflection.Target;

public class DoubleWrapperConverter extends TypeConverter<Double> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == Double.class;
	}

	@Override
	protected Double convert(String stringValue, Target<?> to) {
		return Double.valueOf(stringValue);
	}
}
