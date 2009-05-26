package iogi.parameters;


import iogi.reflection.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parameters {
	private final List<Parameter> parametersList;
	private final Map<String, Parameter> arguments;
	
	public Parameters(Parameter... parameters) {
		this(Arrays.asList(parameters));
	}

	public Parameters(List<Parameter> parametersList) {
		this.parametersList = parametersList;
		this.arguments = parametersByName(parametersList);
	}
	
	private Map<String, Parameter> parametersByName(List<Parameter> parameters) {
		Map<String, Parameter> arguments = new HashMap<String, Parameter>();
		
		for (Parameter parameter : parameters) {
			arguments.put(parameter.getName(), parameter);
		}
		
		return arguments;
	}

	public List<Parameter> getParametersList() {
		return parametersList;
	}

	public Parameter namedAfter(Target<?> target) {
		return arguments.get(target.getName());
	}
	
	public Parameters relevantTo(Target<?> target) {
		ArrayList<Parameter> relevant = new ArrayList<Parameter>(getParametersList().size());
		
		for (Parameter parameter : getParametersList()) {
			if (parameter.getFirstNameComponent().equals(target.getName()))
				relevant.add(parameter);
		}
		
		return new Parameters(relevant);
	}

	public Parameters strip() {
		ArrayList<Parameter> striped = new ArrayList<Parameter>(getParametersList().size());
		
		for (Parameter parameter : getParametersList()) {
			striped.add(parameter.strip());
		}
		
		return new Parameters(striped);
	}
	
	@Override
	public String toString() {
		return "Parameters" + parametersList.toString();
	}
}