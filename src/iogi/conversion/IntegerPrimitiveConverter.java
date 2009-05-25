package iogi.conversion;

import iogi.reflection.Target;

public class IntegerPrimitiveConverter extends TypeConverter<Integer> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == int.class;
	}

	@Override
	protected Integer convert(String stringValue, Target<?> to) {
		return new IntegerWrapperConverter().convert(stringValue, to);
	}
}
