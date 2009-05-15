package iogi.conversion;

import iogi.Instantiatior;
import iogi.Parameter;
import iogi.Primitives;
import iogi.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectConverter implements TypeConverter<Object> {
	private final Instantiatior instantiator;

	public ObjectConverter(Instantiatior instantiator) {
		this.instantiator = instantiator;
	}
	
	@Override
	public boolean isAbleToConvertTo(Class<?> type) {
		return !Primitives.isPrimitiveLike(type);
	}

	@Override
	public Object convert(String stringRepresentation, Target<?> target, Map<String, String> arguments) {
		List<Parameter> parameters = new ArrayList<Parameter>(); 
		for (Map.Entry<String, String> argument : arguments.entrySet()) {
			parameters.add(new Parameter(argument.getKey(), argument.getValue()));
		}
		return instantiator.instantiate(target, parameters);
	}

}
