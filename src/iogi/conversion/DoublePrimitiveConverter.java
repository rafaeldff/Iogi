package iogi.conversion;

import iogi.reflection.Target;

public class DoublePrimitiveConverter extends TypeConverter<Double> {
	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == double.class;
	}

	@Override
	protected Double convert(final String stringValue, final Target<?> to) {
		return new DoubleWrapperConverter().convert(stringValue, to);
	}
}
