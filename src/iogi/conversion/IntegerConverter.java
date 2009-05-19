package iogi.conversion;


import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

public class IntegerConverter implements Instantiator<Integer> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == int.class || target.getClassType() == Integer.class;
	}

	@Override
	public Integer instantiate(Target<?> target, Parameters parameters) {
		return Integer.valueOf(parameters.namedAfter(target).getValue());
	}

}
