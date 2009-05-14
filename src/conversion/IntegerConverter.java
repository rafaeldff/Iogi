package conversion;

public class IntegerConverter implements Converter<Integer> {

	@Override
	public boolean isAbleToConvertTo(Class<?> type) {
		return type == int.class || type == Integer.class;
	}

	@Override
	public Integer convert(String stringRepresentation, Class<?> type) {
		return Integer.valueOf(stringRepresentation);
	}

}
