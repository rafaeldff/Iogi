package iogi.conversion;

import iogi.reflection.Target;

public class CharacterWrapperConverter extends TypeConverter<Character> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == Character.class;
	}

	@Override
	protected Character convert(String stringValue, Target<?> to) {
		if (stringValue.length() != 1)
			throw new ConversionException("Cannot convert string \"%s\" to a single character.", stringValue);
		return stringValue.charAt(0);
	}
}
