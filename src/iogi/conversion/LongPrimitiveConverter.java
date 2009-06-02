package iogi.conversion;

import iogi.reflection.Target;

public class LongPrimitiveConverter extends TypeConverter<Long> {

	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == long.class;
	}

	@Override
	protected Long convert(final String stringValue, final Target<?> to) {
		return new LongWrapperConverter().convert(stringValue, to);
	}

}
