package iogi.conversion;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.math.BigDecimal;

public class BigDecimalConveter implements Instantiator<BigDecimal> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == BigDecimal.class;
	}
	
	@Override
	public BigDecimal instantiate(Target<?> target, Parameters parameters) {
		return new BigDecimal(parameters.namedAfter(target).getValue());
	}
}
