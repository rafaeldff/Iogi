/**
 * 
 */
package br.com.caelum.iogi.collections;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

import com.google.common.base.Function;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;

class ParametersByIndex {
	private static final Pattern firstComponentPattern = Pattern.compile("[^\\[]+\\[(\\d+)\\]");
	private static final Function<Parameter, Integer> EXTRACT_INDEX_OR_RETURN_NEGATIVE = new Function<Parameter, Integer>() {
    public Integer apply(Parameter parameter) {
      final Matcher matcher = firstComponentPattern.matcher(parameter.getFirstNameComponentWithDecoration());
      return matcher.find() ? Integer.valueOf(matcher.group(1)) : -1;
    }
  };
  
	private final ListMultimap<Integer, Parameter> firstComponentToParameterMap;
	
	public ParametersByIndex(final Parameters parameters, final Target<?> target) {
		this.firstComponentToParameterMap = groupByIndex(parameters, target);
	}

	private ListMultimap<Integer, Parameter> groupByIndex(final Parameters parameters, final Target<?> target) {
    final List<Parameter> relevant = parameters.forTarget(target);
    final List<Parameter> sorted = Ordering.natural().onResultOf(EXTRACT_INDEX_OR_RETURN_NEGATIVE).sortedCopy(relevant);
    final ListMultimap<Integer, Parameter> map = LinkedListMultimap.create();
    
    for (final Parameter parameter : sorted) {
      final Integer index = EXTRACT_INDEX_OR_RETURN_NEGATIVE.apply(parameter);
      if (index >= 0)
        map.put(index, parameter);
    }
    
		return Multimaps.unmodifiableListMultimap(map);
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