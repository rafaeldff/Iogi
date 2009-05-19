package iogi;

import static com.google.common.base.Predicates.equalTo;
import iogi.exceptions.InvalidTypeException;
import iogi.exceptions.NoConstructorFoundException;
import iogi.reflection.ClassConstructor;
import iogi.reflection.Target;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

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
		
		Parameters relevantParameters = parameters.relevant(target).strip();
		Set<ClassConstructor> candidateConstructors = target.classConstructors();  
		
		ClassConstructor desiredConstructor = desiredConstructor(relevantParameters);
		Set<ClassConstructor> matchingConstructors = findMatchingConstructors(candidateConstructors, desiredConstructor);
		signalErrorIfNoMatchingConstructorFound(target, matchingConstructors, desiredConstructor);
		
		ClassConstructor firstMatchingConstructor = matchingConstructors.iterator().next();
		
		return firstMatchingConstructor.instantiate(argumentInstantiator, relevantParameters);
	}

	private <T> void signalErrorIfTargetIsAbstract(Target<T> target) {
		if (!target.isInstantiable())
			throw new InvalidTypeException("Cannot instantiate abstract type %s", target.getClassType());
	}

	private ClassConstructor desiredConstructor(Parameters relevantParameters) {
		HashSet<String> givenParameterNames = new HashSet<String>();
		for (Parameter paremeter : relevantParameters.getParametersList()) {
			givenParameterNames.add(paremeter.getFirstNameComponent());
		}
		return new ClassConstructor(givenParameterNames);
	}

	private Set<ClassConstructor> findMatchingConstructors(Set<ClassConstructor> candidateConstructors, ClassConstructor targetConstructor) {
		return Sets.filter(candidateConstructors, equalTo(targetConstructor));
	}

	private <T> void signalErrorIfNoMatchingConstructorFound(Target<T> target, Set<ClassConstructor> matchingConstructors, ClassConstructor desiredConstructor) {
		if (matchingConstructors.isEmpty())
			throw new NoConstructorFoundException("No constructor found to instantiate a %s named %s " +
					"with parameter names %s",
					target.getClassType(), target.getName(), desiredConstructor);
	}

}
