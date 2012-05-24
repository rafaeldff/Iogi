package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;


public class DoublePrimitiveConverter extends TypeConverter<Double> {
	public boolean isAbleToInstantiate(final Target<?> target, Parameters parameters) {
		return target.getClassType() == double.class;
	}

	@Override
	protected Double convert(final String stringValue, final Target<?> to) {
		return new DoubleWrapperConverter().convert(stringValue, to);
	}
}
