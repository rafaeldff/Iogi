package br.com.caelum.iogi.reflection;

import br.com.caelum.iogi.DependenciesInjector;
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

    private Collection<ClassConstructor> classConstructors;
    private DependenciesInjector dependenciesInjector;

    public Constructors(Collection<ClassConstructor> constructors, DependenciesInjector dependenciesInjector) {
        classConstructors = constructors;
        this.dependenciesInjector = dependenciesInjector;        
    }

    public Constructors compatibleWith(final Parameters relevantParameters) {
        final LinkedList<ClassConstructor> compatible = new LinkedList<ClassConstructor>();

        for (final ClassConstructor classConstructor : classConstructors) {
            if (classConstructor.canInstantiateOrInject(relevantParameters, dependenciesInjector))
                compatible.add(classConstructor);
        }

        return new Constructors(compatible, dependenciesInjector);
    }

    public ClassConstructor largest() {
        if (classConstructors.isEmpty()) {
            return ClassConstructor.nullClassConstructor();
        }
        
        return orderConstructorsBySize.max(classConstructors);
    }
}