package iogi;

import iogi.parameters.Parameters;
import iogi.reflection.Primitives;
import iogi.reflection.Target;


public class ObjectConverter implements Instantiator<Object> {
	private final Iogi instantiator;

	public ObjectConverter(final Iogi instantiator) {
		this.instantiator = instantiator;
	}
	
	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return !Primitives.isPrimitiveLike(target.getClassType());
	}

	@Override
	public Object instantiate(final Target<?> target, final Parameters parameters) {
		return instantiator.instantiate(target, parameters);
	}
}
