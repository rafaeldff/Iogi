package iogi.conversion;


import iogi.Parameters;
import iogi.Target;


public class DoubleConverter implements TypeConverter<Double> {
	@Override
	public boolean isAbleToConvertTo(Class<?> type) {
		return type == double.class || type == Double.class;
	}

	@Override
	public Double convert(String stringRepresentation, Target<?> target, Parameters parameters) {
		return Double.valueOf(stringRepresentation);
	}

}
