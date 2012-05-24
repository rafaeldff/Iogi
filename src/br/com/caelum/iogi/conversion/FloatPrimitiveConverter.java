package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;


public class FloatPrimitiveConverter extends TypeConverter<Float> {

	public boolean isAbleToInstantiate(final Target<?> target, Parameters parameters) {
		return target.getClassType() == float.class;
	}

	@Override
	protected Float convert(final String stringValue, final Target<?> to) {
		return new FloatWrapperConverter().convert(stringValue, to);
	}

}
