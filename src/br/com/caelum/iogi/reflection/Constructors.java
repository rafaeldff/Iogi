package br.com.caelum.iogi.reflection;

import br.com.caelum.iogi.DependenciesInjector;
import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.util.Ints;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Constructors {
    public static final Ordering<ClassConstructor> orderConstructorsBySize = new Ordering<ClassConstructor>() {
        public int compare(final ClassConstructor first, final ClassConstructor second) {
            return Ints.compare(first.size(), second.size());
        }
    };

    private final Collection<ClassConstructor> classConstructors;
    private final DependenciesInjector dependenciesInjector;

    public Constructors(final Collection<ClassConstructor> constructors, final DependenciesInjector dependenciesInjector) {
        this.classConstructors = constructors;
        this.dependenciesInjector = dependenciesInjector;
    }

    public FilledConstructors compatibleWith(final Parameters relevantParameters) {
        final LinkedList<ClassConstructor> compatible = new LinkedList<ClassConstructor>();

        for (final ClassConstructor classConstructor : classConstructors) {
            if (classConstructor.canInstantiateOrInject(relevantParameters, dependenciesInjector))
                compatible.add(classConstructor);
        }

        return new FilledConstructors(compatible, relevantParameters, dependenciesInjector);
    }

    public int size() {
        return classConstructors.size();
    }

    public static class FilledConstructors {
        private final Collection<ClassConstructor> classConstructors;
        private final Parameters parameters;
        private final DependenciesInjector dependenciesInjector;

        public FilledConstructors(final Collection<ClassConstructor> classConstructors, final Parameters parameters, final DependenciesInjector dependenciesInjector) {
            this.classConstructors = classConstructors;
            this.parameters = parameters;
            this.dependenciesInjector = dependenciesInjector;
        }

        public FilledConstructor largest() {
            if (classConstructors.isEmpty()) {
                return FilledConstructor.nullFilledConstructor();
            }

            final ClassConstructor largestConstructor = orderConstructorsBySize.max(classConstructors);
            return new FilledConstructor(largestConstructor, parameters, dependenciesInjector);
        }

    }

    public static class FilledConstructor {
        private final ClassConstructor constructor;
        private final Parameters parameters;
        private final DependenciesInjector dependenciesInjector;

        public FilledConstructor(final ClassConstructor value, final Parameters parameters, final DependenciesInjector dependenciesInjector) {
            this.constructor = value;
            this.parameters = parameters;
            this.dependenciesInjector = dependenciesInjector;
        }

        public NewObject instantiate(final Instantiator<Object> argumentInstantiator) {
            final Collection<Target<?>> needDependency = constructor.notFulfilledBy(parameters); //TODO: Refactor this method

            final List<Object> argumentValues = Lists.newArrayList();
            for (final Target<?> target : constructor.argumentTargets()) {
                Object value = needDependency.contains(target) ? dependenciesInjector.provide(target) : argumentInstantiator.instantiate(target, parameters);
                argumentValues.add(value);
            }

            Object newObjectValue = constructor.construct(argumentValues);
            return new NewObject(argumentInstantiator, constructor, parameters, newObjectValue);
        }

        private static FilledConstructor nullFilledConstructor() {
            return new FilledConstructor(null,null, null) {
                @Override
                public NewObject instantiate(final Instantiator<Object> argumentInstantiator) {
                    return NewObject.nullNewObject();
                }
            };
        }
    }

}