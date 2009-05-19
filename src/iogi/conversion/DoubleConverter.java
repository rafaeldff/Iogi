package iogi.conversion;


import iogi.Instantiator;
import iogi.Parameters;
import iogi.Target;


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
