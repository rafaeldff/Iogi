package iogi.conversion;

import iogi.reflection.Target;

public class BooleanWrapperConverter extends TypeConverter<Boolean> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == Boolean.class;
	}
	
	@Override
	protected Boolean convert(String stringValue, Target<?> to) {
		return Boolean.parseBoolean(stringValue);
	}
}
