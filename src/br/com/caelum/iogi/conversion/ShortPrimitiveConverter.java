package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.reflection.Target;


public class ShortPrimitiveConverter extends TypeConverter<Short> {

	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == short.class;
	}

	@Override
	protected Short convert(final String stringValue, final Target<?> to) {
		return new ShortWrapperConverter().convert(stringValue, to);
	}

}
