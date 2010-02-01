/**
 * 
 */
package br.com.caelum.iogi.collections;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

class ParametersByIndex {
	private static final Pattern firstComponentPattern = Pattern.compile("[^\\[]+\\[(\\d+)\\]");
	private final ListMultimap<Integer, Parameter> firstComponentToParameterMap;
	
	public ParametersByIndex(final Parameters parameters, final Target<?> target) {
		this.firstComponentToParameterMap = groupByIndex(parameters, target);
	}

	private ListMultimap<Integer, Parameter> groupByIndex(final Parameters parameters, final Target<?> target) {
		final ListMultimap<Integer, Parameter> map = LinkedListMultimap.create();
		for (final Parameter parameter : parameters.forTarget(target)) {
			final Integer index = extractIndexOrReturnNull(parameter);
			if (index != null) 
				map.put(index, parameter);
		}
		return Multimaps.unmodifiableListMultimap(map);
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