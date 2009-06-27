package iogi.reflection;

import iogi.DependenciesInjector;
import iogi.Instantiator;
import iogi.parameters.Parameters;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.vidageek.mirror.dsl.Mirror;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;

/**
 * Equality based on constructor names; 
 *
 */
public class ClassConstructor {
	private static final CachingParanamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());
	private final Set<String> names;
	private final Constructor<?> constructor;
	
	private ClassConstructor(final Constructor<?> constructor, final Set<String> parameterNames) {
		this.constructor = constructor;
		this.names = parameterNames;
	}
	
	public ClassConstructor(final Constructor<?> constructor) {
		this(constructor, parameterNames(constructor));
	}
	
	public ClassConstructor(final Set<String> parameterNames) {
		this(null, parameterNames);
	}
	
	private static Set<String> parameterNames(final Constructor<?> constructor) {
		final String[] lookedUpNames = paranamer.lookupParameterNames(constructor);
		
		final HashSet<String> parameterNames = new HashSet<String>(Arrays.asList(lookedUpNames));
		
		return parameterNames;
	}
	
	public Set<String> getNames() {
		return names;
	}
	
	public int size() {
		return names.size();
	}

	public Object instantiate(final Instantiator<?> instantiator, final Parameters parameters, final DependenciesInjector dependenciesInjector) {
		final List<Object> argumentValues = Lists.newArrayList();
		final Collection<Target<?>> needDependency = notFulfilledBy(parameters);
		
		for (final Target<?> target : parameterTargets()) {
			Object value;
			if (needDependency.contains(target))
				value = dependenciesInjector.provide(target);
			else
				value = instantiator.instantiate(target, parameters);
			
			argumentValues.add(value);
		}

		return new Mirror().on(declaringClass()).invoke().constructor(constructor).withArgs(argumentValues.toArray());
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
		final Type[] parameterTypes = constructor.getGenericParameterTypes();
		final String[] parameterNames = paranamer.lookupParameterNames(constructor);
		
		final ArrayList<Target<?>> targets = Lists.newArrayList();
		for (int i = 0; i < parameterNames.length; i++) {
			final String name = parameterNames[i];
			targets.add(new Target<Object>(parameterTypes[i], name));
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
