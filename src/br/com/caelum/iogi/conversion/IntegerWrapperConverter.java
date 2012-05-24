package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;


public class IntegerWrapperConverter extends TypeConverter<Integer> {
	public boolean isAbleToInstantiate(final Target<?> target, Parameters parameters) {
		return  target.getClassType() == Integer.class;
	}

	@Override
	protected Integer convert(final String stringValue, final Target<?> to) {
		return Integer.valueOf(stringValue);
	}
}
