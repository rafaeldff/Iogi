package iogi.collections;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;
import iogi.util.Assert;

import java.util.ArrayList;
import java.util.List;


public class IndexedListInstantiator implements Instantiator<List<Object>> {

	private final Instantiator<Object> listElementInstantiator;

	public IndexedListInstantiator(final Instantiator<Object> listElementInstantiator) {
		this.listElementInstantiator = listElementInstantiator;
	}

	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return List.class.isAssignableFrom(target.getClassType());
	}
	
	@Override
	public List<Object> instantiate(final Target<?> target, final Parameters parameters) {
		final ParametersByIndex parametersByIndex = new ParametersByIndex(parameters, target);
		
		final ArrayList<Object> newList = new ArrayList<Object>();
		
		final Target<?> elementTarget = elementTarget(target);
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