package iogi.conversion;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

public class CharacterConverter implements Instantiator<Character> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == char.class || target.getClassType() == Character.class;
	}

	@Override
	public Character instantiate(Target<?> target, Parameters parameters) {
		String string = parameters.namedAfter(target).getValue();
		if (string.length() != 1)
			throw new ConversionException("Cannot convert string \"%s\" to a single character.", string);
		return string.charAt(0);
	}
}
