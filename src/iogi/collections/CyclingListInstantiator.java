package iogi.collections;

import iogi.Instantiator;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;
import iogi.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.Lists;

public class CyclingListInstantiator implements Instantiator<List<Object>> {
	private final Instantiator<Object> elementInstantiator;

	public CyclingListInstantiator(final Instantiator<Object> objectInstantiator) {
		this.elementInstantiator = objectInstantiator;
	}

	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return List.class.isAssignableFrom(target.getClassType());
	}

	@Override
	public List<Object> instantiate(final Target<?> target, final Parameters parameters) {
		Assert.isNotARawType(target);
		final Parameters relevantParameters = parameters.relevantTo(target);
		
		final Target<Object> listElementTarget = target.typeArgument(0);
		final Collection<List<Parameter>> parameterLists = breakList(relevantParameters.getParametersList());
		
		final ArrayList<Object> newList = new ArrayList<Object>();
		for (final List<Parameter> parameterListForAnElement : parameterLists) {
			final Object listElement = elementInstantiator.instantiate(listElementTarget, new Parameters(parameterListForAnElement));
			newList.add(listElement);
		}
		
		return newList;
	}

	private Collection<List<Parameter>> breakList(final List<Parameter> parameters) {
		final int listSize = countToFirstRepeatedParameterName(parameters);
		
		if (listSize == 0)
			return Collections.<List<Parameter>>emptyList(); 
		
		return Lists.partition(parameters, listSize);
	}

	private int countToFirstRepeatedParameterName(final List<Parameter> parameters) {
		if (parameters.isEmpty())
			return 0;
		
		int count = 1;
		final ListIterator<Parameter> parametersIterator = parameters.listIterator();
		final String firstParameterName = parametersIterator.next().getName();
		
		while (parametersIterator.hasNext()) {
			if (parametersIterator.next().getName().equals(firstParameterName)) {
				break;
			}
			count++;
		}
		
		return count;
	}

}
