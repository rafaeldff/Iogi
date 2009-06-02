package iogi.collections;

import iogi.Instantiator;
import iogi.exceptions.InvalidTypeException;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.Lists;

public class ListInstantiator implements Instantiator<List<Object>> {
	private final Instantiator<Object> elementInstantiator;

	public ListInstantiator(final Instantiator<Object> objectInstantiator) {
		this.elementInstantiator = objectInstantiator;
	}

	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return List.class.isAssignableFrom(target.getClassType());
	}

	@Override
	public List<Object> instantiate(final Target<?> target, final Parameters parameters) {
		signalErrorIfGivenARawType(target);
			
		final Target<Object> listElementTarget = target.typeArgument(0);
		final Collection<List<Parameter>> parameterLists = breakList(parameters.relevantTo(target).getParametersList());
		
		final ArrayList<Object> newList = new ArrayList<Object>();
		for (final List<Parameter> parameterListForAnElement : parameterLists) {
			final Object listElement = elementInstantiator.instantiate(listElementTarget, new Parameters(parameterListForAnElement));
			newList.add(listElement);
		}
		
		return newList;
	}

	private void signalErrorIfGivenARawType(final Target<?> target) {
		if (!(target.getType() instanceof ParameterizedType))
			throw new InvalidTypeException("Expecting a parameterized list type, got raw type \"%s\" instead", target.getType());
	}

	private Collection<List<Parameter>> breakList(final List<Parameter> parameters) {
		final int listSize = this.countToFirstRepeatedParameterName(parameters);
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
