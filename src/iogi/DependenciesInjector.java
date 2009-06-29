/**
 * 
 */
package iogi;

import iogi.reflection.Target;
import iogi.spi.DependencyProvider;

import java.util.Collection;

public class DependenciesInjector {
	private final DependencyProvider dependencyProvider;

	public DependenciesInjector(final DependencyProvider dependencyProvider) {
		this.dependencyProvider = dependencyProvider;
	}
	
	public boolean canObtainDependenciesFor(final Collection<Target<?>> targets) {
		for (final Target<?> target : targets) {
			if (!dependencyProvider.canProvide(target)) {
				return false;
			}
		}
		return true;
	}
	
	public Object provide(final Target<?> target) {
		return dependencyProvider.provide(target);
	}

}