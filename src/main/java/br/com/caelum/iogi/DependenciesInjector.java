/**
 * 
 */
package br.com.caelum.iogi;

import java.util.Collection;

import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;

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

    public static DependenciesInjector nullDependenciesInjector() {
        return new DependenciesInjector(new NullDependencyProvider());
    }
}