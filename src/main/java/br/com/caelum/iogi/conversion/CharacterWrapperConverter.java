package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.exceptions.ConversionException;
import br.com.caelum.iogi.reflection.Target;

public class CharacterWrapperConverter extends TypeConverter<Character> {
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == Character.class;
	}

	@Override
	protected Character convert(final String stringValue, final Target<?> to) {
		if (stringValue.length() != 1)
			throw new ConversionException("Cannot convert string \"%s\" to a single character (in order to fulfill %s).", stringValue, to);
		return stringValue.charAt(0);
	}
}
