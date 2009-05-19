package iogi.conversion;

import iogi.Instantiator;
import iogi.Iogi;
import iogi.Parameters;
import iogi.Primitives;
import iogi.Target;


public class ObjectConverter implements Instantiator<Object> {
	private final Iogi instantiator;

	public ObjectConverter(Iogi instantiator) {
		this.instantiator = instantiator;
	}
	
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return !Primitives.isPrimitiveLike(target.getClassType());
	}

	@Override
	public Object instantiate(Target<?> target, Parameters parameters) {
		return instantiator.instantiate(target, parameters);
	}
}
