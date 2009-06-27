/**
 * 
 */
package iogi.collections;

import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

class ParametersByIndex {
	private static final Pattern firstComponentPattern = Pattern.compile("[^\\[]+\\[(\\d+)\\]");
	private final ListMultimap<Integer, Parameter> firstComponentToParameterMap;
	
	public ParametersByIndex(final Parameters parameters, final Target<?> target) {
		this.firstComponentToParameterMap = groupByIndex(parameters, target);
	}

	private ArrayListMultimap<Integer, Parameter> groupByIndex(final Parameters parameters, final Target<?> target) {
		final ArrayListMultimap<Integer, Parameter> map = ArrayListMultimap.create();
		for (final Parameter parameter : parameters.getParametersList(target)) {
			final Integer index = extractIndexOrReturnNull(parameter);
			if (index != null) 
				map.put(index, parameter);
		}
		return map;
	}

	private Integer extractIndexOrReturnNull(final Parameter parameter) {
		final Matcher matcher = firstComponentPattern.matcher(parameter.getFirstNameComponentWithDecoration());			
		return matcher.find() ? Integer.valueOf(matcher.group(1)) : null;
	}

	public Collection<Integer> indexes() {
		return firstComponentToParameterMap.keySet();
	}

	public boolean isEmpty() {
		return firstComponentToParameterMap.isEmpty();
	}

	public Parameters at(final int index) {
		return new Parameters(firstComponentToParameterMap.get(index));
	}

	public int count() {
		return firstComponentToParameterMap.values().size();
	}
}