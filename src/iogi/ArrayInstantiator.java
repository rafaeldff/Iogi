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
		ParametersByFirstComponent byFirstComponent = new ParametersByFirstComponent(parameters);
		
		Object[] newArray = makeNewArray(target, byFirstComponent);
		
		populateNewArray(newArray, target, byFirstComponent);
		
		return cast(newArray);
	}

	private Object[] makeNewArray(Target<?> target, ParametersByFirstComponent byFirstComponent) {
		return (Object[])Array.newInstance(target.arrayElementType(), byFirstComponent.groupCount());
	}

	private void populateNewArray(Object[] newArray, Target<?> target, ParametersByFirstComponent byFirstComponent) {
		for (int i = 0; i < newArray.length; i++) {
			newArray[i] = instantiateElement(target, byFirstComponent, i);
		}
	}

	private T instantiateElement(Target<?> target, ParametersByFirstComponent byFirstComponent,	int index) {
		String firstComponent = target.getName() + "["+index+"]";
		Target<?> elementTarget = Target.create(target.arrayElementType(), firstComponent);
		Parameters elementParameters = byFirstComponent.get(firstComponent);
		return elementInstantiator.instantiate(elementTarget, elementParameters);
	}
	
	@SuppressWarnings("unchecked")
	private T[] cast(Object[] newArray) {
		return (T[])newArray;
	}
	
	private static class ParametersByFirstComponent {

		private ListMultimap<String, Parameter> firstComponentToParameterMap;
		
		public ParametersByFirstComponent(Parameters parameters) {
			this.firstComponentToParameterMap = ArrayListMultimap.create(); 
			for (Parameter parameter : parameters.getParametersList()) {
				firstComponentToParameterMap.put(parameter.getFirstNameComponent(), parameter);
			}
		}
		
		public int groupCount() {
			return firstComponentToParameterMap.keySet().size();
		}

		public Parameters get(String firstComponent) {
			return new Parameters(firstComponentToParameterMap.get(firstComponent));
		}
	}
}
