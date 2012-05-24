package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;


public class ByteWrapperConverter extends TypeConverter<Byte> {
	public boolean isAbleToInstantiate(final Target<?> target, Parameters parameters) {
		return target.getClassType() == Byte.class;
	}

	@Override
	protected Byte convert(final String stringValue, final Target<?> to) {
		return Byte.valueOf(stringValue);
	}
}