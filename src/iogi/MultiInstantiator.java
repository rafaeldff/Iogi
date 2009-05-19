package iogi;

import iogi.exceptions.InvalidTypeException;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.util.Collection;

public class MultiInstantiator implements Instantiator<Object> {

	private Collection<Instantiator<?>> instantiators;
	
	public MultiInstantiator(Collection<Instantiator<?>> instantiators) {
		this.instantiators = instantiators;
	}

	@Override
	public Object instantiate(Target<?> target, Parameters parameters) {
		for (Instantiator<?> instantiator : instantiators) {
			if (instantiator.isAbleToInstantiate(target))
				return instantiator.instantiate(target, parameters);
		}
		throw new InvalidTypeException("Cannot instantiate " + target);
	}

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		for (Instantiator<?> instantiator : instantiators) {
			if (instantiator.isAbleToInstantiate(target))
				return true;
		}
		return false;
	}
}
