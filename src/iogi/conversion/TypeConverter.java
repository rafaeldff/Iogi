package iogi.conversion;

import java.util.Map;

import iogi.Target;

public interface TypeConverter<T> {
	public boolean isAbleToConvertTo(Class<?> type);
	public T convert(String stringRepresentation, Target<?> target, Map<String, String> arguments);
}
