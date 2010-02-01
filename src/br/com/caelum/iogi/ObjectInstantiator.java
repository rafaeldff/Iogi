package br.com.caelum.iogi;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.vidageek.mirror.dsl.Matcher;
import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.iogi.exceptions.InvalidTypeException;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.ClassConstructor;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.iogi.spi.ParameterNamesProvider;

import com.google.common.collect.Lists;

public class ObjectInstantiator implements Instantiator<Object> {
	private static final Mirror MIRROR = new Mirror();
	private final Instantiator<Object> argumentInstantiator;
	private final DependenciesInjector dependenciesInjector;
	private final ParameterNamesProvider parameterNamesProvider;
	
	public ObjectInstantiator(final Instantiator<Object> argumentInstantiator, final DependencyProvider dependencyProvider, final ParameterNamesProvider parameterNamesProvider) {
		this.argumentInstantiator = argumentInstantiator;
		this.dependenciesInjector = new DependenciesInjector(dependencyProvider);
		this.parameterNamesProvider = parameterNamesProvider;
	}

	public boolean isAbleToInstantiate(final Target<?> target) {
		return true;
	}

	public Object instantiate(final Target<?> target, final Parameters parameters) {
		expectingAConcreteTarget(target);
		
		final Parameters strippedParameters = parameters.strip(target);
		
		final Collection<ClassConstructor> compatibleConstructors = target.compatibleConstructors(strippedParameters, dependenciesInjector, parameterNamesProvider);
		if (compatibleConstructors.isEmpty()) {
			return null;
		}
		
		final List<ClassConstructor> orderedConstructors = fromLargestToSmallest(compatibleConstructors);
		final ClassConstructor largestMatchingConstructor = orderedConstructors.iterator().next();
		
		final Object object = largestMatchingConstructor.instantiate(argumentInstantiator, strippedParameters, dependenciesInjector);
		populateRemainingProperties(object, largestMatchingConstructor, strippedParameters);
		
		return object;
	}

	private <T> void expectingAConcreteTarget(final Target<T> target) {
		if (!target.isInstantiable())
			throw new InvalidTypeException("Cannot instantiate abstract type %s", target.getClassType());
	}

	private List<ClassConstructor> fromLargestToSmallest(final Collection<ClassConstructor> matchingConstructors) {
		final ArrayList<ClassConstructor> constructors = Lists.newArrayList(matchingConstructors);
		Collections.sort(constructors, new Comparator<ClassConstructor>(){
			public int compare(final ClassConstructor first, final ClassConstructor second) {
				return first.size() < second.size() ? 1 : (first.size() == second.size() ? 0 : -1);
			}
		});
		return Collections.unmodifiableList(constructors);
	}
	
	private void populateRemainingProperties(final Object object, final ClassConstructor constructor, final Parameters parameters) {
		final Parameters remainingParameters = parameters.notUsedBy(constructor);
		for (final Setter setter : settersIn(object)) {
			final Target<?> target = new Target<Object>(setter.type(), setter.propertyName());
			if (remainingParameters.hasRelatedTo(target)) {
				final Object argument = argumentInstantiator.instantiate(target, parameters);
				setter.set(argument);
			}
		}
	}
	
	private Collection<Setter> settersIn(final Object object) {
		final ArrayList<Setter> foundSetters = new ArrayList<Setter>();
		for (final Method setterMethod: MIRROR.on(object.getClass()).reflectAll().methodsMatching(SETTERS)) {
			foundSetters.add(new Setter(setterMethod, object));
		}
		return Collections.unmodifiableList(foundSetters);
	}
	
	private static final Matcher<Method> SETTERS = new Matcher<Method>(){
		public boolean accepts(final Method method) {
			return method.getName().startsWith("set");
		}
	};

	private static class Setter {
		private final Method setter;
		private final Object object;
		
		public Setter(final Method setter, final Object object) {
			this.setter = setter;
			this.object = object;
		}
		
		public void set(final Object argument) {
			MIRROR.on(object).invoke().method(setter).withArgs(argument);
		}
		
		public String propertyName() {
			final String capitalizedPropertyName = setter.getName().substring(3);
			final String propertyName = capitalizedPropertyName.substring(0, 1).toLowerCase() + capitalizedPropertyName.substring(1);
			return propertyName;
		}
		
		public Type type() {
			return setter.getGenericParameterTypes()[0];
		}
		
		@Override
		public String toString() {
			return "Setter(" + propertyName() +")";
		}
	}
}
