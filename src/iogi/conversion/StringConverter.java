package iogi.conversion;

import iogi.Parameter;
import iogi.Parameters;
import iogi.Target;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringConverter implements TypeConverter<String> {

	@Override
	public boolean isAbleToConvertTo(Class<?> type) {
		return type == String.class;
	}
	
	@Override
	public String convert(String stringRepresentation, Target<?> target, Parameters parameters) {
		return arguments(parameters.getParametersList()).get(target.getName());
	}

	private Map<String, String> arguments(List<Parameter> parameters) {
		Map<String, String> arguments = new HashMap<String, String>();
		
		for (Parameter parameter : parameters) {
			String argumentName = parameter.getName();
			String argumentValue = parameter.getValue();
			arguments.put(argumentName, argumentValue);
		}
		
		return arguments;
	}
}
