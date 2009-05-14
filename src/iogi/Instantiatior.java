package iogi;

import static java.util.Collections.singleton;
import iogi.conversion.Converter;
import iogi.conversion.DoubleConverter;
import iogi.conversion.IntegerConverter;
import iogi.conversion.TypeConverter;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class Instantiatior {
	private Converter converter = new Converter(Collections.unmodifiableList(Arrays.<TypeConverter<?>>asList(new IntegerConverter(), new DoubleConverter())));

	public <T> T instantiate(Class<T> rootClass, Parameter... parameters) {
		return instantiate(rootClass, Arrays.asList(parameters));
	}	
	
	public <T> T instantiate(Class<T> rootClass, List<Parameter> parameters) {
		Map<String, String> arguments = arguments(parameters);
		ClassConstructor targetConstructor = targetConstructorFromArguments(arguments);
		
		Set<ClassConstructor> candidateConstructors = candidateConstructorsInRootClass(rootClass);  
		candidateConstructors.retainAll(singleton(targetConstructor));
		
		ClassConstructor matchingConstructor = candidateConstructors.iterator().next();
		
		return rootClass.cast(matchingConstructor.instantiate(converter, arguments));
	}

	private Map<String, String> arguments(List<Parameter> parameters) {
		Map<String, String> arguments = new HashMap<String, String>();
		
		for (Parameter parameter : parameters) {
			String[] keyComponents = parameter.getName().split("\\.");
			String argumentName = keyComponents[1];
			String argumentValue = parameter.getValue();
			arguments.put(argumentName, argumentValue);
		}
		
		return arguments;
	}
	
	private ClassConstructor targetConstructorFromArguments(Map<String, String> arguments) {
		Set<String> givenParameterNames = arguments.keySet();
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
