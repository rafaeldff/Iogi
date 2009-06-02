package iogi.conversion;

import iogi.reflection.Target;


public class StringConverter extends TypeConverter<String> {
	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == String.class;
	}

	@Override
	protected String convert(final String stringValue, final Target<?> to) {
		return stringValue;
	}
}
