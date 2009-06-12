package iogi.collections;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

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
		final ParametersByIndex parametersByIndex = new ParametersByIndex(parameters.relevantTo(target), target);
		
		final ArrayList<Object> newList = new ArrayList<Object>();
		
		final Target<?> elementTarget = target.typeArgument(0);
		
		for (final Integer index : parametersByIndex.indexes()) {
			final Parameters atIndex = parametersByIndex.get(index);
			final Object newElement = listElementInstantiator.instantiate(elementTarget , atIndex); 
			newList.add(newElement);
		}
		
		return newList;
	}

}
