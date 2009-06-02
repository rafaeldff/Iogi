package iogi.conversion;

import iogi.reflection.Target;

public class LongWrapperConverter extends TypeConverter<Long> {
	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == Long.class;
	}
	
	@Override
	protected Long convert(final String stringValue, final Target<?> to) {
		return Long.valueOf(stringValue);
	}
}
