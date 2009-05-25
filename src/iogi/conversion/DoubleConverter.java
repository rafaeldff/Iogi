package iogi.conversion;

import iogi.reflection.Target;

public class DoubleConverter extends TypeConverter<Double> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == double.class || target.getClassType() == Double.class;
	}

	@Override
	protected Double convert(String stringValue, Target<?> to) {
		return Double.valueOf(stringValue);
	}
}
