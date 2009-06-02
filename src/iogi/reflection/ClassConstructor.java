package iogi.reflection;

import iogi.DependencyProvider;
import iogi.Instantiator;
import iogi.parameters.Parameters;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
		final HashSet<String> parameterNames = new HashSet<String>();
		final String[] lookedUpNames = paranamer.lookupParameterNames(constructor);
		for (final String parameterName : lookedUpNames) {
			if (!parameterName.isEmpty()) //To account for http://jira.codehaus.org/browse/PARANAMER-10
				parameterNames.add(parameterName);
		}
		return parameterNames;
	}
	
	public Set<String> getNames() {
		return names;
	}

	public Object instantiate(final Instantiator<?> instantiator, final Parameters parameters, final DependencyProvider dependencyProvider) {
		final List<Object> argumentValues = Lists.newArrayList();
		final Collection<Target<?>> needDependency = notFulfilledBy(parameters);
		
		for (final Target<?> target : parameterTargets()) {
			Object value;
			if (needDependency.contains(target))
				value = dependencyProvider.provide(target);
			else
				value = instantiator.instantiate(target, parameters);
			
			argumentValues.add(value);
		}

		return new Mirror().on(declaringClass()).invoke().constructor(constructor).withArgs(argumentValues.toArray());
	}

	private Class<?> declaringClass() {
		return constructor.getDeclaringClass();
	}

	public int size() {
		return names.size();
	}

	public Collection<Target<?>> notFulfilledBy(final Parameters parameters) {
		final ArrayList<Target<?>> unfulfilled = Lists.newArrayList();
		
		for (final Target<?> target : parameterTargets()) {
			if (parameters.relevantTo(target).getParametersList().isEmpty())
				unfulfilled.add(target);
		}
		
		return unfulfilled;
	}
	
	private List<Target<?>> parameterTargets() {
		final Type[] parameterTypes = constructor.getGenericParameterTypes();
		final String[] parameterNames = namesInOrder();
		
		final ArrayList<Target<?>> targets = Lists.newArrayList();
		for (int i = 0; i < parameterNames.length; i++) {
			final String name = parameterNames[i];
			targets.add(new Target<Object>(parameterTypes[i], name));
		}
		
		return Collections.unmodifiableList(targets);
	}	
	
	private String[] namesInOrder() {
		final String[] foundByParanamer = paranamer.lookupParameterNames(constructor);
		
		if (foundByParanamer.length == 1 && foundByParanamer[0].isEmpty())
			return new String[] {}; //To account for http://jira.codehaus.org/browse/PARANAMER-10
		
		return foundByParanamer;
	}
	
	
	@Override
	public String toString() {
		return "(" + Joiner.on(", ").join(names) + ")"; 
	}
}
