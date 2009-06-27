package iogi.util;

import iogi.reflection.Target;
import iogi.spi.DependencyProvider;


public class NullDependencyProvider implements DependencyProvider {
	@Override
	public boolean canProvide(final Target<?> target) {
		return false;
	}

	@Override
	public Object provide(final Target<?> target) {
		return null;
	}
}
