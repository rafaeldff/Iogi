package iogi.conversion;

import iogi.reflection.Target;

public class CharacterPrimitiveConverter extends TypeConverter<Character> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == char.class;
	}

	@Override
	protected Character convert(String stringValue, Target<?> to) {
		return new CharacterWrapperConverter().convert(stringValue, to);
	}

}
