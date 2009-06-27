package iogi.spi;

import iogi.reflection.Target;

public interface DependencyProvider {
	public boolean canProvide(Target<?> target);
	public Object provide(Target<?> target);
}
