package iogi;

import static java.util.Collections.singleton;
import iogi.conversion.Converter;
import iogi.conversion.DoubleConverter;
import iogi.conversion.IntegerConverter;
import iogi.conversion.ObjectConverter;
import iogi.conversion.StringConverter;
import iogi.conversion.TypeConverter;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.collect.ImmutableList;



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
		Map<String, String> arguments = arguments(parameters);
		ClassConstructor targetConstructor = targetConstructorFromArguments(arguments);
		
		Set<ClassConstructor> candidateConstructors = candidateConstructorsInRootClass(target.getType());  
		
		candidateConstructors.retainAll(singleton(targetConstructor));
		ClassConstructor matchingConstructor = candidateConstructors.iterator().next();
		
		return matchingConstructor.instantiate(converter, arguments);
	}

	private Parameter parameterNamed(List<Parameter> parameters, String name) {
		for (Parameter parameter : parameters) {
			if (parameter.getName().equals(name)) {
				return parameter;
			}
		}
		throw new NoSuchElementException("Cannot find parameter named \"" + name + "\"");
	}

	private Map<String, String> arguments(List<Parameter> parameters) {
		Map<String, String> arguments = new HashMap<String, String>();
		
		for (Parameter parameter : parameters) {
			String[] keyComponents = breakKeyComponents(parameter);
			String argumentName = keyComponents[1];
			String argumentValue = parameter.getValue();
			arguments.put(argumentName, argumentValue);
		}
		
		return arguments;
	}

	private String[] breakKeyComponents(Parameter parameter) {
		String name = parameter.getName();
		int firstDot = name.indexOf('.');
		String beforeFirstDot = name.substring(0, firstDot);
		String afterFirstDot = name.substring(firstDot + 1,  name.length());
		return new String[]{beforeFirstDot, afterFirstDot};
	}
	
	
	private ClassConstructor targetConstructorFromArguments(Map<String, String> arguments) {
		HashSet<String> givenParameterNames = new HashSet<String>();
		for (String argument : arguments.keySet()) {
			int firstDot = argument.indexOf('.');
			int cutOff = firstDot == -1 ? argument.length() : firstDot;
			givenParameterNames.add(argument.substring(0, cutOff));
		}
		return new ClassConstructor(givenParameterNames);
	}

	public Set<ClassConstructor> candidateConstructorsInRootClass(Class<?> theClass) {
		HashSet<ClassConstructor> classConstructors = new HashSet<ClassConstructor>();
		for (Constructor<?> constructor : theClass.getConstructors()) {
			classConstructors.add(new ClassConstructor(constructor));
		}
		return classConstructors;
	}
}
