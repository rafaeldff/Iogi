package br.com.caelum.iogi;

import br.com.caelum.iogi.exceptions.InvalidTypeException;
import br.com.caelum.iogi.parameters.Parameters;
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

	public boolean isAbleToInstantiate(final Target<?> target) {
		return true;
	}

	public Object instantiate(final Target<?> target, final Parameters parameters) {
		expectingAConcreteTarget(target);
		
		final Parameters parametersForTarget = parameters.focusedOn(target);

        return target
                .constructors(parameterNamesProvider, dependenciesInjector)
                .compatibleWith(parametersForTarget)
                .largest()
                .instantiate(argumentInstantiator, parametersForTarget)
                .withPropertiesSet(parametersForTarget);
	}

    private <T> void expectingAConcreteTarget(final Target<T> target) {
        if (!target.isInstantiable())
            throw new InvalidTypeException("Cannot instantiate abstract type %s", target.getClassType());
    }

}
