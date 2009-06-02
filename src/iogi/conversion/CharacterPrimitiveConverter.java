package iogi.conversion;

import iogi.reflection.Target;

public class CharacterPrimitiveConverter extends TypeConverter<Character> {

	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == char.class;
	}

	@Override
	protected Character convert(final String stringValue, final Target<?> to) {
		return new CharacterWrapperConverter().convert(stringValue, to);
	}

}
