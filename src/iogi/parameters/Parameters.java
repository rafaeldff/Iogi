package iogi.parameters;


import iogi.reflection.ClassConstructor;
import iogi.reflection.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class Parameters {
	private final List<Parameter> parametersList;
	private final ListMultimap<String, Parameter> parametersByFirstNameComponent;
	
	public Parameters(final Parameter... parameters) {
		this(Arrays.asList(parameters));
	}

	public Parameters(final List<Parameter> parametersList) {
		this.parametersList = parametersList;
		this.parametersByFirstNameComponent = groupByFirstNameComponent(parametersList);
	}
	
	private ListMultimap<String, Parameter> groupByFirstNameComponent(final List<Parameter> parameters) {
		final ListMultimap<String, Parameter> firstNameComponentToParameterMap = ArrayListMultimap.create(); 
		
		for (final Parameter parameter : parameters) {
			firstNameComponentToParameterMap.put(parameter.getFirstNameComponent(), parameter);
		}
		
		return firstNameComponentToParameterMap;
	}

	public List<Parameter> getParametersList(final Target<?> target) {
		return relevantTo(target).parametersList;
	}
	
	List<Parameter> getParametersList() {
		return this.parametersList;
	}
	

	public Parameter namedAfter(final Target<?> target) {
		final Collection<Parameter> named = parametersByFirstNameComponent.get(target.getName());
		assertFoundAtMostOneTarget(target, named);
		return named.isEmpty() ? null : named.iterator().next();
	}

	private void assertFoundAtMostOneTarget(final Target<?> target, final Collection<Parameter> named) {
		if (named.size() > 1)
			throw new IllegalStateException(
					"Expecting only one parameter named after " + target + ", found instead " + named);
	}
	
	public Parameters relevantTo(final Target<?> target) {
		return new Parameters(parametersByFirstNameComponent.get(target.getName()));
	}

	public Parameters strip() {
		final ArrayList<Parameter> striped = new ArrayList<Parameter>(getParametersList().size());
		
		for (final Parameter parameter : getParametersList()) {
			striped.add(parameter.strip());
		}
		
		return new Parameters(striped);
	}
	
	private Set<String> firstComponents() {
		return this.parametersByFirstNameComponent.keySet();
	}

	public Parameters notUsedBy(final ClassConstructor aConstructor) {
		final SetView<String> namesNotUsedBy = Sets.difference(firstComponents(), aConstructor.getNames());
		final List<Parameter> unusedParameters = new ArrayList<Parameter>();
		
		for (final String name : namesNotUsedBy) {
			unusedParameters.addAll(parametersByFirstNameComponent.get(name));
		}
		
		return new Parameters(unusedParameters);
	}
	
	public boolean areEmpty() {
		return parametersList.isEmpty();
	}
	
	public String signatureString() {
		return "(" + Joiner.on(", ").join(getParametersList()) + ")";
	}
	
	@Override
	public String toString() {
		return "Parameters" + signatureString();
	}
	
	@Override
	public int hashCode() {
		return this.getParametersList().hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Parameters))
			return false;
		
		final Parameters other = (Parameters)obj;
		return getParametersList().equals(other.getParametersList());
	}
	
}