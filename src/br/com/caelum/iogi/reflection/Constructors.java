package br.com.caelum.iogi.reflection;

import br.com.caelum.iogi.DependenciesInjector;
import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.util.Ints;
import com.google.common.collect.Ordering;

import java.util.Collection;
import java.util.LinkedList;

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

        return new FilledConstructors(compatible, relevantParameters);
    }

    public int size() {
        return classConstructors.size();
    }

    public static class FilledConstructors {
        private final Collection<ClassConstructor> classConstructors;
        private final Parameters parameters;

        public FilledConstructors(final Collection<ClassConstructor> classConstructors, final Parameters parameters) {
            this.classConstructors = classConstructors;
            this.parameters = parameters;
        }

        public FilledConstructor largest() {
            if (classConstructors.isEmpty()) {
                return FilledConstructor.nullFilledConstructor();
            }

            ClassConstructor largestConstructor = orderConstructorsBySize.max(classConstructors);
            return new FilledConstructor(largestConstructor, parameters);
        }

    }

    public static class FilledConstructor {
        private final ClassConstructor constructor;
        private final Parameters parameters;

        public FilledConstructor(final ClassConstructor value, final Parameters parameters) {
            this.constructor = value;
            this.parameters = parameters;
        }

        public NewObject instantiate(final Instantiator<Object> argumentInstantiator) {
            return constructor.instantiate(argumentInstantiator, parameters);
        }

        private static FilledConstructor nullFilledConstructor() {
            return new FilledConstructor(null,null) {
                @Override
                public NewObject instantiate(final Instantiator<Object> argumentInstantiator) {
                    return NewObject.nullNewObject();
                }
            };
        }
    }

}