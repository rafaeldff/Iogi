package br.com.caelum.iogi.util;

import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;


public class NullDependencyProvider implements DependencyProvider {
	public boolean canProvide(final Target<?> target) {
		return false;
	}

	public Object provide(final Target<?> target) {
		return null;
	}
}
