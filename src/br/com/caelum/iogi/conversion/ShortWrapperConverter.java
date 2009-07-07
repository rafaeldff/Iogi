package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.reflection.Target;


public class ShortWrapperConverter extends TypeConverter<Short> {

	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == Short.class;
	}
	
	@Override
	protected Short convert(final String stringValue, final Target<?> to) {
		return Short.parseShort(stringValue);
	}
}