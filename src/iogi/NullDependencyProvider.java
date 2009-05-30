package iogi;

import iogi.reflection.Target;


public class NullDependencyProvider implements DependencyProvider {
	@Override
	public boolean canProvide(Target<?> target) {
		return false;
	}

	@Override
	public Object provide(Target<?> target) {
		return null;
	}
}
