package iogi;

import iogi.exceptions.InvalidTypeException;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.util.Collection;

public class MultiInstantiator implements Instantiator<Object> {

	private final Collection<Instantiator<?>> instantiators;
	
	public MultiInstantiator(final Collection<Instantiator<?>> instantiators) {
		this.instantiators = instantiators;
	}

	@Override
	public Object instantiate(final Target<?> target, final Parameters parameters) {
		for (final Instantiator<?> instantiator : instantiators) {
			if (instantiator.isAbleToInstantiate(target))
				return instantiator.instantiate(target, parameters);
		}
		throw new InvalidTypeException("Cannot instantiate " + target);
	}

	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		for (final Instantiator<?> instantiator : instantiators) {
			if (instantiator.isAbleToInstantiate(target))
				return true;
		}
		return false;
	}
}
