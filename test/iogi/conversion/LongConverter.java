package iogi.conversion;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

public class LongConverter implements Instantiator<Long> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == Long.class || target.getClassType() == long.class;
	}
	
	@Override
	public Long instantiate(Target<?> target, Parameters parameters) {
		return Long.valueOf(parameters.namedAfter(target).getValue());
	}

}
