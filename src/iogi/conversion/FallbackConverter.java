package iogi.conversion;

import iogi.reflection.Target;

public class FallbackConverter<T> extends TypeConverter<T> {
	private final TypeConverter<T> delegate;
	private final T fallbackValue;

	public static <T> FallbackConverter<T> fallbackToNull(final TypeConverter<T> delegate) {
		return new FallbackConverter<T>(delegate, null);
	}
	
	public static <T> FallbackConverter<T> fallbackTo(final TypeConverter<T> delegate, final T fallbackValue) {
		return new FallbackConverter<T>(delegate, fallbackValue);
	}
	
	public FallbackConverter(final TypeConverter<T> delegate, final T fallbackValue) {
		this.delegate = delegate;
		this.fallbackValue = fallbackValue;
	}

	@Override
	protected T convert(final String stringValue, final Target<?> to) {
		if (stringValue.isEmpty())
			return fallbackValue;
		return delegate.convert(stringValue, to);
	}

	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return delegate.isAbleToInstantiate(target);
	}
}
