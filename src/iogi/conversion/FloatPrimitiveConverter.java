package iogi.conversion;

import iogi.reflection.Target;

public class FloatPrimitiveConverter extends TypeConverter<Float> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == float.class;
	}

	@Override
	protected Float convert(String stringValue, Target<?> to) {
		return new FloatWrapperConverter().convert(stringValue, to);
	}

}
