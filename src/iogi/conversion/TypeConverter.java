package iogi.conversion;


import iogi.Parameters;
import iogi.Target;

public interface TypeConverter<T> {
	public boolean isAbleToConvertTo(Class<?> type);
	public T convert(String stringRepresentation, Target<?> target, Parameters parameters);
}
