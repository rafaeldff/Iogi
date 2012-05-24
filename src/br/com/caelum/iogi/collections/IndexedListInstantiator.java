package br.com.caelum.iogi.collections;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.util.Assert;

import com.google.common.collect.Lists;


public class IndexedListInstantiator implements Instantiator<List<Object>> {

	private final Instantiator<Object> listElementInstantiator;

	public IndexedListInstantiator(final Instantiator<Object> listElementInstantiator) {
		this.listElementInstantiator = listElementInstantiator;
	}

	public boolean isAbleToInstantiate(final Target<?> target, Parameters parameters) {
		return List.class.isAssignableFrom(target.getClassType());
	}
	
	public List<Object> instantiate(final Target<?> target, final Parameters parameters) {
		final ParametersByIndex parametersByIndex = new ParametersByIndex(parameters, target);
		
		final Target<?> elementTarget = elementTarget(target);
		
		final ArrayList<Object> newList = Lists.newArrayListWithExpectedSize(parametersByIndex.count());
		for (final Integer index : parametersByIndex.indexes()) {
			final Parameters atIndex = parametersByIndex.at(index);
			final Object newElement = listElementInstantiator.instantiate(elementTarget , atIndex); 
			newList.add(newElement);
		}
		
		return newList;
	}

	private Target<?> elementTarget(final Target<?> target) {
		Assert.isNotARawType(target);
		return (Target<?>) target.typeArgument(0);
	}
}