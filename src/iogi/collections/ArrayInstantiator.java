package iogi.collections;

import iogi.Instantiator;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.lang.reflect.Array;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		ParametersByIndex parametersByIndex = new ParametersByIndex(parameters, target);
	
		ArrayFactory factory = new ArrayFactory(target, parametersByIndex);
		
		return factory.getArray();
	}
	
	private static class ParametersByIndex {
		private Pattern firstComponentPattern;
		private ListMultimap<Integer, Parameter> firstComponentToParameterMap;
		
		public ParametersByIndex(Parameters parameters, Target<?> target) {
			this.firstComponentPattern = Pattern.compile(target.getName() + "\\[(\\d+)\\]");
			
			this.firstComponentToParameterMap = ArrayListMultimap.create(); 
			for (Parameter parameter : parameters.getParametersList()) {
				Matcher matcher = firstComponentPattern.matcher(parameter.getFirstNameComponentWithDecoration());
				if (matcher.find()) {
					Integer index = Integer.valueOf(matcher.group(1));
					firstComponentToParameterMap.put(index, parameter);
				}
			}
		}

		public int groupCount() {
			return firstComponentToParameterMap.keySet().size();
		}

		public Parameters get(int index) {
			return new Parameters(firstComponentToParameterMap.get(index));
		}
	}
	
	private class ArrayFactory {
		private final ParametersByIndex parametersByIndex;
		private final Target<?> arrayTarget;

		public ArrayFactory(Target<?> target, ParametersByIndex parametersByIndex) {
			this.arrayTarget = target;
			this.parametersByIndex = parametersByIndex;
		}

		public Object getArray() {
			Object array = makeArray();
			
			for (int i = 0; i <  Array.getLength(array); i++) {
				Array.set(array, i, instantiateArrayElement(i));
			}
			
			return array;
		}

		private Object makeArray() {
			return Array.newInstance(arrayTarget.arrayElementType(), parametersByIndex.groupCount());
		}
		
		private Object instantiateArrayElement(int index) {
			Target<?> elementTarget = arrayTarget.arrayElementTarget();
			return elementInstantiator.instantiate(elementTarget, parametersByIndex.get(index));
		}
	}
}
