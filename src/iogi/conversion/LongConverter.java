package iogi.conversion;

import iogi.reflection.Target;

public class LongConverter extends TypeConverter<Long> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == Long.class || target.getClassType() == long.class;
	}
	
	@Override
	protected Long convert(String stringValue, Target<?> to) {
		return Long.valueOf(stringValue);
	}

}
