package br.com.caelum.iogi;

import java.util.Collection;

import br.com.caelum.iogi.exceptions.InvalidTypeException;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

public class MultiInstantiator implements Instantiator<Object> {

	private final Collection<Instantiator<?>> instantiators;
	
	public MultiInstantiator(final Collection<Instantiator<?>> instantiators) {
		this.instantiators = instantiators;
	}

	public Object instantiate(final Target<?> target, final Parameters parameters) {
		for (final Instantiator<?> instantiator : instantiators) {
			if (instantiator.isAbleToInstantiate(target))
				return instantiator.instantiate(target, parameters);
		}
		throw new InvalidTypeException("Cannot instantiate " + target);
	}

	public boolean isAbleToInstantiate(final Target<?> target) {
		for (final Instantiator<?> instantiator : instantiators) {
			if (instantiator.isAbleToInstantiate(target))
				return true;
		}
		return false;
	}
}
