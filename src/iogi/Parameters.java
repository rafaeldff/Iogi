package iogi;


import java.util.Arrays;
import java.util.List;

public class Parameters {
	private List<Parameter> parametersList;
	
	public Parameters(Parameter... parameters) {
		this.parametersList = Arrays.asList(parameters);
	}

	public Parameters(List<Parameter> parametersList) {
		this.parametersList = parametersList;
	}

	public List<Parameter> getParametersList() {
		return parametersList;
	}
}