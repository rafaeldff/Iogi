package iogi;

import iogi.parameters.Parameters;
import iogi.reflection.Primitives;
import iogi.reflection.Target;


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
