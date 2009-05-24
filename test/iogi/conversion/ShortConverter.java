package iogi.conversion;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

public class ShortConverter implements Instantiator<Short> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == short.class || target.getClassType() == Short.class;
	}
	
	@Override
	public Short instantiate(Target<?> target, Parameters parameters) {
		return Short.parseShort(parameters.namedAfter(target).getValue());
	}
}
