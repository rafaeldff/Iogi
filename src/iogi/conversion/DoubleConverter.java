package iogi.conversion;

import java.util.Map;

import iogi.Target;


public class DoubleConverter implements TypeConverter<Double> {
	@Override
	public boolean isAbleToConvertTo(Class<?> type) {
		return type == double.class || type == Double.class;
	}

	@Override
	public Double convert(String stringRepresentation, Target<?> target, Map<String, String> arguments) {
		return Double.valueOf(stringRepresentation);
	}

}
