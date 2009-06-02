package iogi.conversion;

import iogi.reflection.Target;


public class BooleanPrimitiveConverter extends TypeConverter<Boolean> {
	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == boolean.class;
	}
	
	@Override
	protected Boolean convert(final String stringValue, final Target<?> to) {
		return new BooleanWrapperConverter().convert(stringValue, to);
	}
}
