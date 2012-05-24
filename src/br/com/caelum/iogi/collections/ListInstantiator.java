package br.com.caelum.iogi.collections;

import java.util.List;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

public class ListInstantiator implements Instantiator<List<Object>> {
	private final Instantiator<Object> listElementInstantiator;

	public ListInstantiator(final Instantiator<Object> listElementInstantiator) {
		this.listElementInstantiator = listElementInstantiator;
	}

	public boolean isAbleToInstantiate(final Target<?> target, Parameters parameters) {
		return target.getClassType().isAssignableFrom(List.class);
	}

	public List<Object> instantiate(final Target<?> target, final Parameters parameters) {
		final List<Parameter> relevantParameters = parameters.forTarget(target);
		
		if (hasNoRelevantParameters(relevantParameters) || firstParameterIsDecorated(relevantParameters))
			return new IndexedListInstantiator(listElementInstantiator).instantiate(target, parameters);
		else
			return new CyclingListInstantiator(listElementInstantiator).instantiate(target, parameters);
	}

	private boolean firstParameterIsDecorated(final List<Parameter> relevantParameters) {
		return relevantParameters.get(0).isDecorated();
	}

	private boolean hasNoRelevantParameters(final List<Parameter> relevantParameters) {
		return relevantParameters.isEmpty();
	}
}
