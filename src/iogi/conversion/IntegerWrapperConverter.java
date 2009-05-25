package iogi.conversion;

import iogi.reflection.Target;

public class IntegerWrapperConverter extends TypeConverter<Integer> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return  target.getClassType() == Integer.class;
	}

	@Override
	protected Integer convert(String stringValue, Target<?> to) {
		return Integer.valueOf(stringValue);
	}
}
