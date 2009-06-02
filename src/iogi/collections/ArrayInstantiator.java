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

	public ArrayInstantiator(final Instantiator<Object> elementInstantiator) {
		this.elementInstantiator = elementInstantiator;
	}
	
	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType().isArray();
	}
	
	@Override
	public Object instantiate(final Target<?> target, final Parameters parameters) {
		final ParametersByIndex parametersByIndex = new ParametersByIndex(parameters, target);
	
		final ArrayFactory factory = new ArrayFactory(target, parametersByIndex);
		
		return factory.getArray();
	}
	
	private static class ParametersByIndex {
		private final Pattern firstComponentPattern;
		private final ListMultimap<Integer, Parameter> firstComponentToParameterMap;
		
		public ParametersByIndex(final Parameters parameters, final Target<?> target) {
			this.firstComponentPattern = Pattern.compile(target.getName() + "\\[(\\d+)\\]");
			this.firstComponentToParameterMap = ArrayListMultimap.create();
			
			for (final Parameter parameter : parameters.getParametersList()) {
				final Integer index = extractIndexOrReturnNull(parameter);
				if (index != null) 
					firstComponentToParameterMap.put(index, parameter);
			}
		}
		
		private Integer extractIndexOrReturnNull(final Parameter parameter) {
			final Matcher matcher = firstComponentPattern.matcher(parameter.getFirstNameComponentWithDecoration());			
			return matcher.find() ? Integer.valueOf(matcher.group(1)) : null;
		}

		public int groupCount() {
			return firstComponentToParameterMap.keySet().size();
		}

		public Parameters get(final int index) {
			return new Parameters(firstComponentToParameterMap.get(index));
		}
	}
	
	private class ArrayFactory {
		private final ParametersByIndex parametersByIndex;
		private final Target<?> arrayTarget;

		public ArrayFactory(final Target<?> target, final ParametersByIndex parametersByIndex) {
			this.arrayTarget = target;
			this.parametersByIndex = parametersByIndex;
		}

		public Object getArray() {
			final Object array = makeArray();
			
			for (int i = 0; i <  Array.getLength(array); i++) {
				Array.set(array, i, instantiateArrayElement(i));
			}
			
			return array;
		}

		private Object makeArray() {
			return Array.newInstance(arrayTarget.arrayElementType(), parametersByIndex.groupCount());
		}
		
		private Object instantiateArrayElement(final int index) {
			final Target<?> elementTarget = arrayTarget.arrayElementTarget();
			return elementInstantiator.instantiate(elementTarget, parametersByIndex.get(index));
		}
	}
}
