package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.reflection.Target;



public class BooleanPrimitiveConverter extends TypeConverter<Boolean> {
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == boolean.class;
	}
	
	@Override
	protected Boolean convert(final String stringValue, final Target<?> to) {
		return new BooleanWrapperConverter().convert(stringValue, to);
	}
}
