package iogi.conversion;

import iogi.reflection.Target;

public class IntegerPrimitiveConverter extends TypeConverter<Integer> {
	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == int.class;
	}

	@Override
	protected Integer convert(final String stringValue, final Target<?> to) {
		return new IntegerWrapperConverter().convert(stringValue, to);
	}
}
