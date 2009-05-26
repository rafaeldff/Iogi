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
	
		ArrayFactory factory = new ArrayFactory(target, byFirstComponent);
		
		return factory.arrayOfT();
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
	
	private class ArrayFactory {
		private final ParametersByFirstComponent byFirstComponent;
		private final Target<?> target;

		public ArrayFactory(Target<?> target, ParametersByFirstComponent byFirstComponent) {
			this.target = target;
			this.byFirstComponent = byFirstComponent;
		}

		@SuppressWarnings("unchecked")
		public T[] arrayOfT() {
			Object[] newArray = populateNewArray();
			return (T[])newArray;
		}

		private Object[] populateNewArray() {
			Object[] array = makeArray();
			
			for (int i = 0; i < array.length; i++) {
				array[i] = instantiateArrayElement(i);
			}
			
			return array;
		}

		private Object[] makeArray() {
			return (Object[])Array.newInstance(target.arrayElementType(), byFirstComponent.groupCount());
		}
		
		private T instantiateArrayElement(int i) {
			String firstComponent = target.getName() + "["+i+"]";
			Target<?> elementTarget = Target.create(target.arrayElementType(), firstComponent);
			Parameters elementParameters = byFirstComponent.get(firstComponent);
			return elementInstantiator.instantiate(elementTarget, elementParameters);
		}
	}
}
