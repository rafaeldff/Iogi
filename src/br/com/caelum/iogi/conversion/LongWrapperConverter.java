package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;


public class LongWrapperConverter extends TypeConverter<Long> {
	public boolean isAbleToInstantiate(final Target<?> target, Parameters parameters) {
		return target.getClassType() == Long.class;
	}
	
	@Override
	protected Long convert(final String stringValue, final Target<?> to) {
		return Long.valueOf(stringValue);
	}
}
