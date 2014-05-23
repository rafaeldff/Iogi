package br.com.caelum.iogi.spi;

import br.com.caelum.iogi.reflection.Target;

public interface DependencyProvider {
	public boolean canProvide(Target<?> target);
	public Object provide(Target<?> target);
}
