package iogi;

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

	public ListInstantiator(Instantiator<Object> objectInstantiator) {
		this.elementInstantiator = objectInstantiator;
	}

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return List.class.isAssignableFrom(target.getClassType());
	}

	@Override
	public List<Object> instantiate(Target<?> target, Parameters parameters) {
		signalErrorIfGivenARawType(target);
			
		Target<Object> listElementTarget = target.typeArgument(0);
		Collection<List<Parameter>> parameterLists = breakList(parameters.relevantTo(target).getParametersList());
		
		ArrayList<Object> newList = new ArrayList<Object>();
		for (List<Parameter> parameterListForAnElement : parameterLists) {
			Object listElement = elementInstantiator.instantiate(listElementTarget, new Parameters(parameterListForAnElement));
			newList.add(listElement);
		}
		
		return newList;
	}

	private void signalErrorIfGivenARawType(Target<?> target) {
		if (!(target.getType() instanceof ParameterizedType))
			throw new InvalidTypeException("Expecting a parameterized list type, got raw type \"%s\" instead", target.getType());
	}

	private Collection<List<Parameter>> breakList(List<Parameter> parameters) {
		int listSize = this.countToFirstRepeatedParameterName(parameters);
		return Lists.partition(parameters, listSize);
	}

	private int countToFirstRepeatedParameterName(List<Parameter> parameters) {
		if (parameters.isEmpty())
			return 0;
		
		int count = 1;
		ListIterator<Parameter> parametersIterator = parameters.listIterator();
		String firstParameterName = parametersIterator.next().getName();
		
		while (parametersIterator.hasNext()) {
			if (parametersIterator.next().getName().equals(firstParameterName)) {
				break;
			}
			count++;
		}
		
		return count;
	}

}
