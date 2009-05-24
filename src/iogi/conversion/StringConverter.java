package iogi.conversion;

import iogi.reflection.Target;


public class StringConverter extends TypeConverter<String> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == String.class;
	}

	@Override
	public String convert(String stringValue) {
		return stringValue;
	}
}
