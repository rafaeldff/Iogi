package iogi;

import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.lang.reflect.Array;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class ArrayInstantiator<T> implements Instantiator<T[]> {
	private final Instantiator<T> elementInstantiator;

	public ArrayInstantiator(Instantiator<T> elementInstantiator) {
		this.elementInstantiator = elementInstantiator;
	}
	
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType().isArray();
	}

	@Override
	public T[] instantiate(Target<?> target, Parameters parameters) {
		Class<?> elementClassType = target.getClassType().getComponentType();
		ListMultimap<String, Parameter> byFirstComponent = parametersByFirstComponent(parameters);
		int arraySize = byFirstComponent.keySet().size();
		Object[] newArray = (Object[])Array.newInstance(elementClassType, arraySize);
		for (int i = 0; i < arraySize; i++) {
			String firstComponent = target.getName() + "["+i+"]";
			Parameters elementParameters = new Parameters(byFirstComponent.get(firstComponent));
			newArray[i] = instantiateElement(Target.create(elementClassType, firstComponent), elementParameters);
		}
		@SuppressWarnings("unchecked")
		T[] arrayOfT = (T[])newArray;
		return arrayOfT;
	}

	private ListMultimap<String, Parameter> parametersByFirstComponent(Parameters parameters) {
		ListMultimap<String, Parameter> firstComponentToParameterMap = ArrayListMultimap.create(); 
		for (Parameter parameter : parameters.getParametersList()) {
			firstComponentToParameterMap.put(parameter.getFirstNameComponent(), parameter);
		}
		return firstComponentToParameterMap;
	}

	private T instantiateElement(Target<?> target, Parameters parameters) {
		return elementInstantiator.instantiate(target, parameters);
	}
}
