package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;


public class BooleanWrapperConverter extends TypeConverter<Boolean> {
	public boolean isAbleToInstantiate(final Target<?> target, Parameters parameters) {
		return target.getClassType() == Boolean.class;
	}
	
	@Override
	protected Boolean convert(final String stringValue, final Target<?> to) {
		return Boolean.parseBoolean(stringValue);
	}
}
