package iogi.conversion;

import iogi.reflection.Target;

public class ShortWrapperConverter extends TypeConverter<Short> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == Short.class;
	}
	
	@Override
	protected Short convert(String stringValue, Target<?> to) {
		return Short.parseShort(stringValue);
	}
}