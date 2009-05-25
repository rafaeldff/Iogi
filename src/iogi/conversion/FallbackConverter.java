package iogi.conversion;

import iogi.reflection.Target;

public class FallbackConverter<T> extends TypeConverter<T> {
	private final TypeConverter<T> delegate;
	private final T fallbackValue;

	public static <T> FallbackConverter<T> fallback(TypeConverter<T> delegate, T fallbackValue) {
		return new FallbackConverter<T>(delegate, fallbackValue);
	}
	
	public FallbackConverter(TypeConverter<T> delegate, T fallbackValue) {
		this.delegate = delegate;
		this.fallbackValue = fallbackValue;
	}

	@Override
	protected T convert(String stringValue, Target<?> to) {
		if (stringValue.isEmpty())
			return fallbackValue;
		return delegate.convert(stringValue, to);
	}

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return delegate.isAbleToInstantiate(target);
	}
}
