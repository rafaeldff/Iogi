package iogi;

import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class ArrayInstantiator implements Instantiator<Object> {
	private final Instantiator<Object> elementInstantiator;

	public ArrayInstantiator(Instantiator<Object> elementInstantiator) {
		this.elementInstantiator = elementInstantiator;
	}
	
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType().isArray();
	}
	
	@Override
	public Object instantiate(Target<?> target, Parameters parameters) {
		ParametersByFirstComponent byFirstComponent = new ParametersByFirstComponent(parameters, target);
	
		ArrayFactory factory = new ArrayFactory(target, byFirstComponent);
		
		return factory.arrayOfT();
	}
	
	private static class ParametersByFirstComponent {
		private ListMultimap<String, Parameter> firstComponentToParameterMap;
		
		public ParametersByFirstComponent(Parameters parameters, Target<?> target) {
			this.firstComponentToParameterMap = ArrayListMultimap.create(); 
			for (Parameter parameter : relatedParameters(parameters, target)) {
				firstComponentToParameterMap.put(parameter.getFirstNameComponentWithDecoration(), parameter);
			}
		}

		private List<Parameter> relatedParameters(Parameters parameters, Target<?> target) {
			List<Parameter> parametersForArray = new ArrayList<Parameter>();
			for (Parameter parameter : parameters.getParametersList()) {
				if (parameter.getFirstNameComponentWithDecoration().matches(target.getName() + "\\[\\d+\\]"))
					parametersForArray.add(parameter);
			}
			return parametersForArray;
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

		public Object arrayOfT() {
			return populateNewArray();
		}

		private Object populateNewArray() {
			Object array = makeArray();
			
			for (int i = 0; i <  Array.getLength(array); i++) {
				Array.set(array, i, instantiateArrayElement(i));
			}
			
			return array;
		}

		private Object makeArray() {
			return Array.newInstance(target.arrayElementType(), byFirstComponent.groupCount());
		}
		
		private Object instantiateArrayElement(int i) {
			String firstComponent = target.getName();
			Target<?> elementTarget = Target.create(target.arrayElementType(), firstComponent);
			Parameters elementParameters = byFirstComponent.get(firstComponent + "["+i+"]");
			return elementInstantiator.instantiate(elementTarget, elementParameters);
		}
	}
}
