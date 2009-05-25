package iogi.conversion;

import iogi.reflection.Target;


public class StringConverter extends TypeConverter<String> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == String.class;
	}

	@Override
	protected String convert(String stringValue, Target<?> to) {
		return stringValue;
	}
}
