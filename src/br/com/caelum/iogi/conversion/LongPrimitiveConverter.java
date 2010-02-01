package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.reflection.Target;


public class LongPrimitiveConverter extends TypeConverter<Long> {

	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == long.class;
	}

	@Override
	protected Long convert(final String stringValue, final Target<?> to) {
		return new LongWrapperConverter().convert(stringValue, to);
	}

}
