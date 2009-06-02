/**
 * 
 */
package iogi;

import iogi.reflection.Target;
import iogi.util.Quantification;

import java.util.Collection;

import com.google.common.base.Predicate;

public class DependenciesInjector {
	private final DependencyProvider dependencyProvider;

	public DependenciesInjector(DependencyProvider dependencyProvider) {
		this.dependencyProvider = dependencyProvider;
	}
	
	public boolean canObtainDependenciesFor(final Collection<Target<?>> targets) {
		return Quantification.forAll(targets, new Predicate<Target<?>>() {
			public boolean apply(Target<?> input) {
				return dependencyProvider.canProvide(input);
			}
		});
	}
	
	public Object provide(Target<?> target) {
		return dependencyProvider.provide(target);
	}

}