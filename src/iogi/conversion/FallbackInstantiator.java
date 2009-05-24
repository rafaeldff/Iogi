package iogi.conversion;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

public class FallbackInstantiator<T> implements Instantiator<T> {
	private final Instantiator<T> delegate;
	private final T fallbackValue;

	public static <T> FallbackInstantiator<T> fallback(Instantiator<T> delegate, T fallbackValue) {
		return new FallbackInstantiator<T>(delegate, fallbackValue);
	}
	
	public FallbackInstantiator(Instantiator<T> delegate, T fallbackValue) {
		this.delegate = delegate;
		this.fallbackValue = fallbackValue;
	}

	@Override
	public T instantiate(Target<?> target, Parameters parameters) {
		if (parameters.namedAfter(target).getValue().isEmpty())
			return fallbackValue;
		return delegate.instantiate(target, parameters);
	}

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return delegate.isAbleToInstantiate(target);
	}
}
