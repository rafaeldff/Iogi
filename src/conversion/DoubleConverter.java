package conversion;


public class DoubleConverter implements Converter<Double> {
	@Override
	public boolean isAbleToConvertTo(Class<?> type) {
		return type == double.class || type == Double.class;
	}

	@Override
	public Double convert(String stringRepresentation, Class<?> type) {
		return Double.valueOf(stringRepresentation);
	}

}
