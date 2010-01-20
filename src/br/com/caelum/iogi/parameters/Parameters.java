package br.com.caelum.iogi.parameters;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import br.com.caelum.iogi.reflection.ClassConstructor;
import br.com.caelum.iogi.reflection.Target;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class Parameters {
	private final ListMultimap<String, Parameter> parametersByFirstNameComponent;
	
	public Parameters(final Parameter... parameters) {
		this(Arrays.asList(parameters));
	}

	public Parameters(final List<Parameter> parametersList) {
		this.parametersByFirstNameComponent = groupByFirstNameComponent(parametersList);
	}
	
	private ListMultimap<String, Parameter> groupByFirstNameComponent(final List<Parameter> parameters) {
		final ListMultimap<String, Parameter> firstNameComponentToParameterMap = ArrayListMultimap.create(); 
		
		for (final Parameter parameter : parameters) {
			firstNameComponentToParameterMap.put(parameter.getFirstNameComponent(), parameter);
		}
		
		return Multimaps.unmodifiableListMultimap(firstNameComponentToParameterMap);
	}

	public List<Parameter> forTarget(final Target<?> target) {
		return parametersByFirstNameComponent.get(target.getName());
	}
	
	public Parameter namedAfter(final Target<?> target) {
		final Collection<Parameter> named = parametersByFirstNameComponent.get(target.getName());
		assertFoundAtMostOneTarget(target, named);
		return named.isEmpty() ? new Parameter(target.getName(), ""): named.iterator().next();
	}

	private void assertFoundAtMostOneTarget(final Target<?> target, final Collection<Parameter> named) {
		if (named.size() > 1)
			throw new IllegalStateException(
					"Expecting only one parameter named after " + target + ", found instead " + named);
	}
	
	public Parameters strip(final Target<?> target) {
		final List<Parameter> relevantParameters = forTarget(target);
		
		final List<Parameter> striped = Lists.newArrayListWithCapacity(relevantParameters.size());
		
		for (final Parameter parameter : relevantParameters) {
			striped.add(parameter.strip());
		}
		
		return new Parameters(striped);
	}
	
	public Parameters notUsedBy(final ClassConstructor aConstructor) {
		final SetView<String> namesNotUsedBy = Sets.difference(firstComponents(), aConstructor.getNames());
		final List<Parameter> unusedParameters = new ArrayList<Parameter>();
		
		for (final String name : namesNotUsedBy) {
			unusedParameters.addAll(parametersByFirstNameComponent.get(name));
		}
		
		return new Parameters(unusedParameters);
	}
	
	private Set<String> firstComponents() {
		return this.parametersByFirstNameComponent.keySet();
	}
	
	public boolean hasRelatedTo(final Target<?> target) {
		return !forTarget(target).isEmpty();
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
		return this.parametersByFirstNameComponent.hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Parameters))
			return false;
		
		final Parameters other = (Parameters)obj;
		return this.parametersByFirstNameComponent.equals(other.parametersByFirstNameComponent);
	}
	
	/* The only reason this is not private is to help unit tests. */
	Collection<Parameter> getParametersList() {
		return this.parametersByFirstNameComponent.values();
	}
}