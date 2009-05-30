package iogi.parameters;


import iogi.reflection.ClassConstructor;
import iogi.reflection.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class Parameters {
	private final List<Parameter> parametersList;
	private final ListMultimap<String, Parameter> parametersByFirstNameComponent;
	
	public Parameters(Parameter... parameters) {
		this(Arrays.asList(parameters));
	}

	public Parameters(List<Parameter> parametersList) {
		this.parametersList = parametersList;
		this.parametersByFirstNameComponent = groupByFirstNameComponent(parametersList);
	}
	
	private ListMultimap<String, Parameter> groupByFirstNameComponent(List<Parameter> parameters) {
		ListMultimap<String, Parameter> firstNameComponentToParameterMap = ArrayListMultimap.create(); 
		
		for (Parameter parameter : parameters) {
			firstNameComponentToParameterMap.put(parameter.getFirstNameComponent(), parameter);
		}
		
		return firstNameComponentToParameterMap;
	}

	public List<Parameter> getParametersList() {
		return parametersList;
	}

	public Parameter namedAfter(Target<?> target) {
		Collection<Parameter> named = parametersByFirstNameComponent.get(target.getName());
		assertFoundAtMostOneTarget(target, named);
		return named.isEmpty() ? null : named.iterator().next();
	}

	private void assertFoundAtMostOneTarget(Target<?> target, Collection<Parameter> named) {
		if (named.size() > 1)
			throw new IllegalStateException(
					"Expecting only one parameter named after " + target + ", found instead " + named);
	}
	
	public Parameters relevantTo(Target<?> target) {
		return new Parameters(parametersByFirstNameComponent.get(target.getName()));
	}

	public Parameters strip() {
		ArrayList<Parameter> striped = new ArrayList<Parameter>(getParametersList().size());
		
		for (Parameter parameter : getParametersList()) {
			striped.add(parameter.strip());
		}
		
		return new Parameters(striped);
	}
	
	public Collection<ClassConstructor> compatible(Collection<ClassConstructor> candidates) {
		List<ClassConstructor> compatible = Lists.newArrayList();
		
		for (ClassConstructor candidate : candidates)
			if (firstComponents().containsAll(candidate.getNames()))
				compatible.add(candidate);
			
		return Collections.unmodifiableList(compatible);
	}

	private Set<String> firstComponents() {
		return this.parametersByFirstNameComponent.keySet();
	}

	public Parameters notUsedBy(ClassConstructor aConstructor) {
		SetView<String> namesNotUsedBy = Sets.difference(firstComponents(), aConstructor.getNames());
		List<Parameter> unusedParameters = new ArrayList<Parameter>();
		
		for (String name : namesNotUsedBy) {
			unusedParameters.addAll(parametersByFirstNameComponent.get(name));
		}
		
		return new Parameters(unusedParameters);
	}
	
	@Override
	public String toString() {
		return "Parameters" + parametersList.toString();
	}
	
	@Override
	public int hashCode() {
		return this.getParametersList().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Parameters))
			return false;
		
		Parameters other = (Parameters)obj;
		return getParametersList().equals(other.getParametersList());
	}
}