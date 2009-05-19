package iogi.conversion;


import iogi.Parameters;
import iogi.Target;

public class IntegerConverter implements TypeConverter<Integer> {

	@Override
	public boolean isAbleToConvertTo(Class<?> type) {
		return type == int.class || type == Integer.class;
	}

	@Override
	public Integer convert(String stringRepresentation, Target<?> target, Parameters parameters) {
		return Integer.valueOf(stringRepresentation);
	}

}
