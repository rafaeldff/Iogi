package iogi.conversion;

public interface TypeConverter<T> {
	public boolean isAbleToConvertTo(Class<?> type);
	public T convert(String stringRepresentation, Class<?> type);
}
