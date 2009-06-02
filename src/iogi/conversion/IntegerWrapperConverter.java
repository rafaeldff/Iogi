package iogi.conversion;

import iogi.reflection.Target;

public class IntegerWrapperConverter extends TypeConverter<Integer> {
	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return  target.getClassType() == Integer.class;
	}

	@Override
	protected Integer convert(final String stringValue, final Target<?> to) {
		return Integer.valueOf(stringValue);
	}
}
