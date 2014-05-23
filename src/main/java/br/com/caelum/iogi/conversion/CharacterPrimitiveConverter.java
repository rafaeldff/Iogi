package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.reflection.Target;


public class CharacterPrimitiveConverter extends TypeConverter<Character> {
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == char.class;
	}

	@Override
	protected Character convert(final String stringValue, final Target<?> to) {
		return new CharacterWrapperConverter().convert(stringValue, to);
	}
}
