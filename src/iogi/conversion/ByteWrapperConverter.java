package iogi.conversion;

import iogi.reflection.Target;

public class ByteWrapperConverter extends TypeConverter<Byte> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == Byte.class;
	}

	@Override
	protected Byte convert(String stringValue, Target<?> to) {
		return Byte.valueOf(stringValue);
	}
}