package br.com.caelum.iogi;

import br.com.caelum.iogi.exceptions.InvalidTypeException;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.ClassConstructor;
import br.com.caelum.iogi.reflection.Constructors.FilledConstructors;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.iogi.spi.ParameterNamesProvider;

public class ObjectInstantiator implements Instantiator<Object> {

    private final Instantiator<Object> argumentInstantiator;
    private final DependenciesInjector dependenciesInjector;
    private final ParameterNamesProvider parameterNamesProvider;

    public ObjectInstantiator(final Instantiator<Object> argumentInstantiator, final DependencyProvider dependencyProvider, final ParameterNamesProvider parameterNamesProvider) {
		this.argumentInstantiator = argumentInstantiator;
		this.dependenciesInjector = new DependenciesInjector(dependencyProvider);
		this.parameterNamesProvider = parameterNamesProvider;
	}

	public boolean isAbleToInstantiate(final Target<?> target, Parameters parameters) {
	   Parameters parametersForTarget = parameters.focusedOn(target);
		final FilledConstructors constructors = targetConstructor(target, parametersForTarget);
      if (constructors.isEmpty()) {
		   return false;
		}
      for (ClassConstructor constructor : constructors.getClassConstructors()) {
         if (canFulfill(constructor, parametersForTarget))
            return true;
      }
      return false;
	}

   private boolean canFulfill(ClassConstructor constructor, Parameters parameters) {
      for (Target<?> argumentTarget : constructor.argumentTargets()) {
         if (!argumentInstantiator.isAbleToInstantiate(argumentTarget, parameters))
            return false;
      }
      return true;
   }

   public Object instantiate(final Target<?> target, final Parameters parameters) {
		expectingAConcreteTarget(target);
		
		return targetConstructor(target, parameters.focusedOn(target))
                .largest()
                .instantiate(argumentInstantiator)
                .valueWithPropertiesSet();
	}

   private FilledConstructors targetConstructor(final Target<?> target, final Parameters parametersForTarget) {
      return target
                .constructors(parameterNamesProvider, dependenciesInjector)
                .compatibleWith(parametersForTarget);
   }

    private <T> void expectingAConcreteTarget(final Target<T> target) {
        if (!target.isInstantiable())
            throw new InvalidTypeException("Cannot instantiate abstract type %s", target.getClassType());
    }

}
