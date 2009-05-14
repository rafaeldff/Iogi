package iogi.conversion;

public class IntegerConverter implements TypeConverter<Integer> {

	@Override
	public boolean isAbleToConvertTo(Class<?> type) {
		return type == int.class || type == Integer.class;
	}

	@Override
	public Integer convert(String stringRepresentation, Class<?> type) {
		return Integer.valueOf(stringRepresentation);
	}

}
