package iogi.conversion;


import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;


public class DoubleConverter implements Instantiator<Double> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == double.class || target.getClassType() == Double.class;
	}

	@Override
	public Double instantiate(Target<?> target, Parameters parameters) {
		return Double.valueOf(parameters.namedAfter(target).getValue());
	}

}
