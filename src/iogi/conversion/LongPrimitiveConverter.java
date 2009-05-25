package iogi.conversion;

import iogi.reflection.Target;

public class LongPrimitiveConverter extends TypeConverter<Long> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == long.class;
	}

	@Override
	protected Long convert(String stringValue, Target<?> to) {
		return new LongWrapperConverter().convert(stringValue, to);
	}

}
