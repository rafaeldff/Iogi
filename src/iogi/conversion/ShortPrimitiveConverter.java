package iogi.conversion;

import iogi.reflection.Target;

public class ShortPrimitiveConverter extends TypeConverter<Short> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == short.class;
	}

	@Override
	protected Short convert(String stringValue, Target<?> to) {
		return new ShortWrapperConverter().convert(stringValue, to);
	}

}
