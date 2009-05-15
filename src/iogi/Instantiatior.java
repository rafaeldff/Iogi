package iogi;


import static com.google.common.base.Predicates.equalTo;
import iogi.conversion.Converter;
import iogi.conversion.DoubleConverter;
import iogi.conversion.IntegerConverter;
import iogi.conversion.ObjectConverter;
import iogi.conversion.StringConverter;
import iogi.conversion.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.collect.ImmutableList;
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
		Object object = target.isPrimitiveLike() ?  instantiatePrimitive(target, parameters) : instantiateObject(target, parameters);
		return target.cast(object);
	}

	private <T> Object instantiatePrimitive(Target<T> target, List<Parameter> parameters) {
		Parameter parameter = parameterNamed(parameters, target.getName());
		return converter.convert(parameter.getValue(), target, null);
	}
	
	private <T> Object instantiateObject(Target<T> target, List<Parameter> parameters) {
		List<Parameter> relevantParameters = relevantParameters(parameters, target);
		Set<ClassConstructor> candidateConstructors = target.classConstructors();  
		
		ClassConstructor desiredConstructor = desiredConstructor(relevantParameters);
		Set<ClassConstructor> foundConstructors = findMatchingConstructors(candidateConstructors, desiredConstructor);
		
		ClassConstructor firstMatchingConstructor = foundConstructors.iterator().next();
		
		return firstMatchingConstructor.instantiate(converter, relevantParameters);
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
		throw new NoSuchElementException("Cannot find parameter named \"" + name + "\"");
	}
}
