package iogi.conversion;

import java.util.Map;

import iogi.Target;

public class StringConverter implements TypeConverter<String> {

	@Override
	public boolean isAbleToConvertTo(Class<?> type) {
		return type == String.class;
	}
	
	@Override
	public String convert(String stringRepresentation, Target<?> target, Map<String, String> arguments) {
		return stringRepresentation;
	}

}
