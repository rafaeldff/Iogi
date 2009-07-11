package br.com.caelum.iogi.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.iogi.DependenciesInjector;
import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameters;
import static br.com.caelum.iogi.util.IogiCollections.*;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;

/**
 * Equality based on constructor names; 
 *
 */
public class ClassConstructor {
	private static final Mirror MIRROR = new Mirror();
	private static final CachingParanamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());
	private final LinkedHashSet<String> names;
	private final Constructor<?> constructor;
	
	public ClassConstructor(final Constructor<?> constructor) {
		this(constructor, parameterNames(constructor));
	}
	
	private ClassConstructor(final Constructor<?> constructor, final LinkedHashSet<String> parameterNames) {
		this.constructor = constructor;
		this.names = parameterNames;
	}
	
	private static LinkedHashSet<String> parameterNames(final Constructor<?> constructor) {
		final String[] lookedUpNames = paranamer.lookupParameterNames(constructor);
		return Sets.newLinkedHashSet(Arrays.asList(lookedUpNames));
	}
	
	public Set<String> getNames() {
		return names;
	}
	
	public int size() {
		return names.size();
	}

	public Object instantiate(final Instantiator<?> instantiator, final Parameters parameters, final DependenciesInjector dependenciesInjector) {
		final List<Object> argumentValues = Lists.newArrayListWithCapacity(size());
		final Collection<Target<?>> needDependency = notFulfilledBy(parameters);
		
		for (final Target<?> target : parameterTargets()) {
			Object value;
			if (needDependency.contains(target))
				value = dependenciesInjector.provide(target);
			else
				value = instantiator.instantiate(target, parameters);
			
			argumentValues.add(value);
		}

		return MIRROR.on(declaringClass()).invoke().constructor(constructor).withArgs(argumentValues.toArray());
	}

	public Collection<Target<?>> notFulfilledBy(final Parameters parameters) {
		final ArrayList<Target<?>> unfulfilledParameterTargets = new ArrayList<Target<?>>();
		
		for (final Target<?> parameterTarget : parameterTargets()) {
			if (!parameters.hasRelatedTo(parameterTarget))
				unfulfilledParameterTargets.add(parameterTarget);
		}
		return Collections.unmodifiableList(unfulfilledParameterTargets);
	}
	
	private List<Target<?>> parameterTargets() {
		final Iterator<Type> typesIterator = Iterators.forArray(constructor.getGenericParameterTypes());
		final Iterator<String> namesIterator = names.iterator();
		final Iterator<Entry<Type, String>> parametersIterator = zip(typesIterator, namesIterator);
		
		final ArrayList<Target<?>> targets = Lists.newArrayList();
		while (parametersIterator.hasNext()) {
			final Entry<Type, String> parameter = parametersIterator.next();
			targets.add(new Target<Object>(parameter.getKey(), parameter.getValue()));
		}
		
		return Collections.unmodifiableList(targets);
	}	
	
	private Class<?> declaringClass() {
		return constructor.getDeclaringClass();
	}
	
	@Override
	public String toString() {
		return "(" + Joiner.on(", ").join(names) + ")"; 
	}
}
