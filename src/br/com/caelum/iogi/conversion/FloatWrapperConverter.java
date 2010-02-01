package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.reflection.Target;


public class FloatWrapperConverter extends TypeConverter<Float> {

	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == Float.class;
	}
	
	@Override
	protected Float convert(final String stringValue, final Target<?> to) {
		return Float.valueOf(stringValue);
	}

}
