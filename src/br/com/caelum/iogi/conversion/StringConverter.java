package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.reflection.Target;



public class StringConverter extends TypeConverter<String> {
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == String.class;
	}

	@Override
	protected String convert(final String stringValue, final Target<?> to) {
		return stringValue;
	}
}
