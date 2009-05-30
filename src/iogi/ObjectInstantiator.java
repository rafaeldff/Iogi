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
import java.util.Set;

import net.vidageek.mirror.dsl.Mirror;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ObjectInstantiator implements Instantiator<Object> {
	private Instantiator<Object> argumentInstantiator;
	
	public ObjectInstantiator(Instantiator<Object> argumentInstantiator) {
		this.argumentInstantiator = argumentInstantiator;
	}

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return !Primitives.isPrimitiveLike(target.getClassType()) && target.getClassType() != String.class;
	}

	public Object instantiate(Target<?> target, Parameters parameters) {
		signalErrorIfTargetIsAbstract(target);
		
		Parameters relevantParameters = parameters.relevantTo(target).strip();
		Set<ClassConstructor> candidateConstructors = target.classConstructors();  
		
		Set<ClassConstructor> matchingConstructors = relevantParameters.compatible(candidateConstructors);
		signalErrorIfNoMatchingConstructorFound(target, matchingConstructors, relevantParameters);
		
		List<ClassConstructor> orderedMatchingConstructors = fromLargestToSmallest(matchingConstructors);
		ClassConstructor largestMatchingConstructor = orderedMatchingConstructors.iterator().next();
		
		Object object = largestMatchingConstructor.instantiate(argumentInstantiator, relevantParameters);
		populateRemainingProperties(object, largestMatchingConstructor, relevantParameters);
		
		return object;
	}

	private <T> void signalErrorIfTargetIsAbstract(Target<T> target) {
		if (!target.isInstantiable())
			throw new InvalidTypeException("Cannot instantiate abstract type %s", target.getClassType());
	}

	private <T> void signalErrorIfNoMatchingConstructorFound(Target<?> target, Set<ClassConstructor> matchingConstructors, Parameters relevantParameters) {
		if (matchingConstructors.isEmpty()) {
			String parameterList =  "(" + Joiner.on(", ").join(relevantParameters.getParametersList()) + ")";
			throw new NoConstructorFoundException("No constructor found to instantiate a %s named %s " +
					"with parameter names %s",
					target.getClassType(), target.getName(), parameterList);
		}
	}
	
	private List<ClassConstructor> fromLargestToSmallest(Set<ClassConstructor> matchingConstructors) {
		ArrayList<ClassConstructor> constructors = Lists.newArrayList(matchingConstructors);
		Collections.sort(constructors, new Comparator<ClassConstructor>(){
			public int compare(ClassConstructor first, ClassConstructor second) {
				return first.size() < second.size() ? 1 : (first.size() == second.size() ? 0 : -1);
			}
		});
		return Collections.unmodifiableList(constructors);
	}
	
	private void populateRemainingProperties(Object object, ClassConstructor constructor, Parameters parameters) {
		Parameters remainingParameters = parameters.notUsedBy(constructor);
		for (Setter setter : settersIn(object)) {
			Target<?> target = new Target<Object>(setter.type(), setter.propertyName());
			Parameters parameterNamedAfterProperty = remainingParameters.relevantTo(target);
			if (parameterNamedAfterProperty != null) {
				Object argument = argumentInstantiator.instantiate(target, remainingParameters);
				setter.set(argument);
			}
		}
	}
	
	private Collection<Setter> settersIn(Object object) {
		ArrayList<Setter> foundSetters = new ArrayList<Setter>();
		for (Method setterMethod: new Mirror().on(object.getClass()).reflectAll().setters()) {
			foundSetters.add(new Setter(setterMethod, object));
		}
		return foundSetters;
	}

	private static class Setter {
		private final Method setter;
		private final Object object;
		
		public Setter(Method setter, Object object) {
			this.setter = setter;
			this.object = object;
		}
		
		public void set(Object argument) {
			new Mirror().on(object).invoke().method(setter).withArgs(argument);
		}
		
		public String propertyName() {
			String capitalizedPropertyName = setter.getName().substring(3);
			String propertyName = capitalizedPropertyName.substring(0, 1).toLowerCase() + capitalizedPropertyName.substring(1);
			return propertyName;
		}
		
		public Type type() {
			return setter.getGenericParameterTypes()[0];
		}
	}
}
