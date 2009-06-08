package iogi;

import iogi.exceptions.InvalidTypeException;
import iogi.exceptions.NoConstructorFoundException;
import iogi.parameters.Parameters;
import iogi.reflection.ClassConstructor;
import iogi.reflection.Primitives;
import iogi.reflection.Target;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ObjectInstantiator implements Instantiator<Object> {
	private final Instantiator<Object> argumentInstantiator;
	private final DependenciesInjector dependenciesInjector;
	
	public ObjectInstantiator(final Instantiator<Object> argumentInstantiator, final DependencyProvider dependencyProvider) {
		this.argumentInstantiator = argumentInstantiator;
		this.dependenciesInjector = new DependenciesInjector(dependencyProvider);
	}

	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return !Primitives.isPrimitiveLike(target.getClassType()) && target.getClassType() != String.class;
	}

	public Object instantiate(final Target<?> target, final Parameters parameters) {
		expectingAConcreteTarget(target);
		
		final Parameters relevantParameters = parameters.relevantTo(target).strip();
		
		final Collection<ClassConstructor> compatibleConstructors = target.compatibleConstructors(relevantParameters, dependenciesInjector);
		expectingAtLeastOneCompatibleConstructor(compatibleConstructors, target, relevantParameters);
		
		List<ClassConstructor> orderedConstructors = fromLargestToSmallest(compatibleConstructors);
		final ClassConstructor largestMatchingConstructor = orderedConstructors.iterator().next();
		
		final Object object = largestMatchingConstructor.instantiate(argumentInstantiator, relevantParameters, dependenciesInjector);
		populateRemainingProperties(object, largestMatchingConstructor, relevantParameters);
		
		return object;
	}

	private <T> void expectingAConcreteTarget(final Target<T> target) {
		if (!target.isInstantiable())
			throw new InvalidTypeException("Cannot instantiate abstract type %s", target.getClassType());
	}

	private <T> void expectingAtLeastOneCompatibleConstructor(final Collection<ClassConstructor> matchingConstructors, final Target<?> target, final Parameters relevantParameters) {
		if (matchingConstructors.isEmpty()) {
			final String parameterList =  "(" + Joiner.on(", ").join(relevantParameters.getParametersList()) + ")";
			throw new NoConstructorFoundException("No constructor found to instantiate a %s named %s " +
					"with parameter names %s",
					target.getClassType(), target.getName(), parameterList);
		}
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
			final Parameters parameterNamedAfterProperty = remainingParameters.relevantTo(target);
			if (parameterNamedAfterProperty != null) {
				final Object argument = argumentInstantiator.instantiate(target, parameterNamedAfterProperty);
				setter.set(argument);
			}
		}
	}
	
	private Collection<Setter> settersIn(final Object object) {
		final ArrayList<Setter> foundSetters = new ArrayList<Setter>();
		for (final Method setterMethod: new Mirror().on(object.getClass()).reflectAll().setters()) {
			foundSetters.add(new Setter(setterMethod, object));
		}
		return foundSetters;
	}

	private static class Setter {
		private final Method setter;
		private final Object object;
		
		public Setter(final Method setter, final Object object) {
			this.setter = setter;
			this.object = object;
		}
		
		public void set(final Object argument) {
			new Mirror().on(object).invoke().method(setter).withArgs(argument);
		}
		
		public String propertyName() {
			final String capitalizedPropertyName = setter.getName().substring(3);
			final String propertyName = capitalizedPropertyName.substring(0, 1).toLowerCase() + capitalizedPropertyName.substring(1);
			return propertyName;
		}
		
		public Type type() {
			return setter.getGenericParameterTypes()[0];
		}
	}
}
