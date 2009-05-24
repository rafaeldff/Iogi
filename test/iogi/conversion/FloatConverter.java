package iogi.conversion;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

public class FloatConverter implements Instantiator<Float> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == Float.class || target.getClassType() == Float.class;
	}
	
	@Override
	public Float instantiate(Target<?> target, Parameters parameters) {
		return Float.valueOf(parameters.namedAfter(target).getValue());
	}

}
