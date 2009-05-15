package iogi.conversion;

import java.util.Map;

import iogi.Target;

public class IntegerConverter implements TypeConverter<Integer> {

	@Override
	public boolean isAbleToConvertTo(Class<?> type) {
		return type == int.class || type == Integer.class;
	}

	@Override
	public Integer convert(String stringRepresentation, Target<?> target, Map<String, String> arguments) {
		return Integer.valueOf(stringRepresentation);
	}

}
