package iogi;


import static com.google.common.base.Predicates.equalTo;
import iogi.conversion.Converter;
import iogi.conversion.DoubleConverter;
import iogi.conversion.IntegerConverter;
import iogi.conversion.ObjectConverter;
import iogi.conversion.StringConverter;
import iogi.conversion.TypeConverter;
import iogi.exceptions.InvalidTypeException;
import iogi.exceptions.NoConstructorFoundException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;



public class Instantiatior {
	private List<TypeConverter<?>> typeConverters = ImmutableList.<TypeConverter<?>>builder()
		.add(new IntegerConverter())
		.add(new DoubleConverter())
		.add(new StringConverter())
		.add(new ObjectConverter(this))
		.build(); 
	private Converter converter =  new Converter(typeConverters);

	public <T> T instantiate(Target<T> target, Parameter... parameters) {
		return instantiate(target, Arrays.asList(parameters));
	}
	
	public <T> T instantiate(Target<T> target, List<Parameter> parameters) {
		Object object;
		if (target.isPrimitiveLike())
			object = instantiatePrimitive(target, parameters);
		else if (isList(target))
			object = instantiateList(target, parameters);
		else
			object = instantiateObject(target, parameters);
		return target.cast(object);
	}

	private <T> Object instantiatePrimitive(Target<T> target, List<Parameter> parameters) {
		signalErrorIfTargetIsVoid(target);
		Parameter parameter = parameterNamed(parameters, target.getName());
		return converter.convert(parameter.getValue(), target, null);
	}
	
	private void signalErrorIfTargetIsVoid(Target<?> target) {
		if (target.getClassType() ==  Void.class)
			throw new InvalidTypeException("Cannot instantiate Void"); 
	}

	private <T> Object instantiateObject(Target<T> target, List<Parameter> parameters) {
		signalErrorIfTargetIsAbstract(target);
		
		List<Parameter> relevantParameters = relevantParameters(parameters, target);
		Set<ClassConstructor> candidateConstructors = target.classConstructors();  
		
		ClassConstructor desiredConstructor = desiredConstructor(relevantParameters);
		Set<ClassConstructor> matchingConstructors = findMatchingConstructors(candidateConstructors, desiredConstructor);
		signalErrorIfNoMatchingConstructorFound(target, matchingConstructors, desiredConstructor);
		
		ClassConstructor firstMatchingConstructor = matchingConstructors.iterator().next();
		
		return firstMatchingConstructor.instantiate(converter, relevantParameters);
	}

	private <T> void signalErrorIfTargetIsAbstract(Target<T> target) {
		if (!target.isInstantiable())
			throw new InvalidTypeException("Cannot instantiate abstract type %s", target.getClassType());
	}

	private <T> void signalErrorIfNoMatchingConstructorFound(Target<T> target,
			Set<ClassConstructor> matchingConstructors, ClassConstructor desiredConstructor) {
		if (matchingConstructors.isEmpty())
			throw new NoConstructorFoundException("No constructor found to instantiate a %s named %s " +
					"with parameter names %s",
					target.getClassType(), target.getName(), desiredConstructor);
	}
	
	private List<Parameter> relevantParameters(List<Parameter> parameters, Target<?> target) {
		ArrayList<Parameter> relevant = new ArrayList<Parameter>(parameters.size());
		for (Parameter parameter : parameters) {
			if (parameter.getFirstNameComponent().equals(target.getName()))
				relevant.add(parameter.strip());
		}
		return relevant;
	}

	private ClassConstructor desiredConstructor(Collection<Parameter> parameters) {
		HashSet<String> givenParameterNames = new HashSet<String>();
		for (Parameter paremeter : parameters) {
			givenParameterNames.add(paremeter.getFirstNameComponent());
		}
		return new ClassConstructor(givenParameterNames);
	}

	private Set<ClassConstructor> findMatchingConstructors(Set<ClassConstructor> candidateConstructors,
			ClassConstructor targetConstructor) {
		return Sets.filter(candidateConstructors, equalTo(targetConstructor));
	}

	private Parameter parameterNamed(List<Parameter> parameters, String name) {
		for (Parameter parameter : parameters) {
			if (parameter.getName().equals(name)) {
				return parameter;
			}
		}
		throw new NoSuchElementException("Cannot find any parameter named \"" + name + "\"");
	}
	
	private boolean isList(Target<?> target) {
		return List.class.isAssignableFrom(target.getClassType());
	}
	
	private Object instantiateList(Target<?> target, List<Parameter> parameters) {
		if (!(target.getType() instanceof ParameterizedType))
			throw new InvalidTypeException("Expecting a parameterized list type, got raw type \"%s\" instead", target.getType());
			
		Target<Object> listElementTarget = findListElementTarget(target);
		Collection<List<Parameter>> parameterLists = breakList(parameters);
		
		ArrayList<Object> newList = new ArrayList<Object>();
		for (List<Parameter> parameterListForAnElement : parameterLists) {
			Object listElement = instantiate(listElementTarget, parameterListForAnElement);
			newList.add(listElement);
		}
		
		return newList;
	}

	private Target<Object> findListElementTarget(Target<?> target) {
		ParameterizedType listType = (ParameterizedType)target.getType();
		Type typeArgument = listType.getActualTypeArguments()[0];
		Target<Object> listElementTarget = new Target<Object>(typeArgument, target.getName());
		return listElementTarget;
	}
	
	private Collection<List<Parameter>> breakList(List<Parameter> parameters) {
		int listSize = countToFirstRepeatedParameterName(parameters);
		return Lists.partition(parameters, listSize);
	}

	private int countToFirstRepeatedParameterName(List<Parameter> parameters) {
		if (parameters.isEmpty())
			return 0;
		
		int count = 1;
		ListIterator<Parameter> parametersIterator = parameters.listIterator();
		String firstParameterName = parametersIterator.next().getName();
		
		while (parametersIterator.hasNext()) {
			if (parametersIterator.next().getName().equals(firstParameterName)) {
				break;
			}
			count++;
		}
		
		return count;
	}
}
