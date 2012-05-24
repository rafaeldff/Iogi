package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;


public class BytePrimitiveConverter extends TypeConverter<Byte> {
	public boolean isAbleToInstantiate(final Target<?> target, Parameters parameters) {
		return target.getClassType() == byte.class;
	}

	@Override
	protected Byte convert(final String stringValue, final Target<?> to) {
		return new ByteWrapperConverter().convert(stringValue, to);
	}

}
