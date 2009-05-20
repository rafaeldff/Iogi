package iogi.conversion;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.math.BigInteger;

public class BigIntegerConverter implements Instantiator<BigInteger> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == BigInteger.class;
	}
	
	@Override
	public BigInteger instantiate(Target<?> target, Parameters parameters) {
		return new BigInteger(parameters.namedAfter(target).getValue());
	}
}
