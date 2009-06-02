package iogi.collections;

import iogi.Instantiator;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
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
		final ParametersByIndex parametersByIndex = new ParametersByIndex(parameters.relevantTo(target), target);
	
		final ArrayFactory factory = new ArrayFactory(target, parametersByIndex);
		
		return factory.getArray();
	}
	
	private static class ParametersByIndex {
		private final Pattern firstComponentPattern;
		private final ListMultimap<Integer, Parameter> firstComponentToParameterMap;
		
		public ParametersByIndex(final Parameters parameters, final Target<?> target) {
			this.firstComponentPattern = indexedNamePattern(target);
			this.firstComponentToParameterMap = groupByIndex(parameters);
		}

		private ArrayListMultimap<Integer, Parameter> groupByIndex(final Parameters parameters) {
			ArrayListMultimap<Integer, Parameter> map = ArrayListMultimap.create();
			for (final Parameter parameter : parameters.getParametersList()) {
				final Integer index = extractIndexOrReturnNull(parameter);
				if (index != null) 
					map.put(index, parameter);
			}
			return map;
		}

		private Pattern indexedNamePattern(final Target<?> target) {
			return Pattern.compile("[^\\[]+\\[(\\d+)\\]");
		}
		
		private Integer extractIndexOrReturnNull(final Parameter parameter) {
			final Matcher matcher = firstComponentPattern.matcher(parameter.getFirstNameComponentWithDecoration());			
			return matcher.find() ? Integer.valueOf(matcher.group(1)) : null;
		}

		public int highestIndex() {
			return Collections.max(firstComponentToParameterMap.keySet());
		}

		public Parameters get(final int index) {
			return new Parameters(firstComponentToParameterMap.get(index));
		}
		
		public Collection<Integer> indexes() {
			return firstComponentToParameterMap.keySet();
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
			
			for (int i : parametersByIndex.indexes()) {
				Array.set(array, i, instantiateArrayElement(i));
			}
			
			return array;
		}

		private Object makeArray() {
			int arrayLength = parametersByIndex.highestIndex() + 1;
			return Array.newInstance(arrayTarget.arrayElementType(), arrayLength);
		}
		
		private Object instantiateArrayElement(final int index) {
			final Target<?> elementTarget = arrayTarget.arrayElementTarget();
			return elementInstantiator.instantiate(elementTarget, parametersByIndex.get(index));
		}
	}
}
