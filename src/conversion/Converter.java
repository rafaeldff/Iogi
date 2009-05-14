package conversion;

public interface Converter<T> {
	public boolean isAbleToConvertTo(Class<?> type);
	public T convert(String stringRepresentation, Class<?> type);
}
