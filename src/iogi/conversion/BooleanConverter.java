package iogi.conversion;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

public class BooleanConverter implements Instantiator<Boolean> {
	@Override
	public Boolean instantiate(Target<?> target, Parameters parameters) {
		return Boolean.parseBoolean(parameters.namedAfter(target).getValue());
	}
	
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == Boolean.class || target.getClassType() == boolean.class;
	}
}
