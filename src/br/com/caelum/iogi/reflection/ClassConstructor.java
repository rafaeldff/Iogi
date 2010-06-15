package br.com.caelum.iogi.reflection;

import br.com.caelum.iogi.DependenciesInjector;
import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.spi.ParameterNamesProvider;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.vidageek.mirror.dsl.Mirror;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

import static br.com.caelum.iogi.util.IogiCollections.zip;

public class ClassConstructor {
    public static ClassConstructor nullClassConstructor() {
        return new ClassConstructor(null,Collections.<String>emptySet(), DependenciesInjector.nullDependenciesInjector()) {
            @Override
            public NewObject instantiate(Instantiator<?> argumentsInstantiator, Parameters parameters) {
                return NewObject.nullNewObject();
            }
        };
    }

    private DependenciesInjector dependenciesInjector;
    private final Set<String> names;
	private final Constructor<?> constructor;

    public ClassConstructor(final Constructor<?> constructor, final ParameterNamesProvider parameterNamesProvider, DependenciesInjector dependenciesInjector) {
        this(constructor, Sets.newLinkedHashSet(parameterNamesProvider.lookupParameterNames(constructor)), dependenciesInjector);
    }

	private ClassConstructor(final Constructor<?> constructor, final Set<String> names, DependenciesInjector dependenciesInjector) {
        this.constructor = constructor;
        this.names = names;
        this.dependenciesInjector = dependenciesInjector;
    }

	public Set<String> getNames() {
		return names;
	}

	public int size() {
		return names.size();
	}

	public NewObject instantiate(final Instantiator<?> argumentsInstantiator, final Parameters parameters) {
		final List<Object> argumentValues = Lists.newArrayListWithCapacity(size());
		final Collection<Target<?>> needDependency = notFulfilledBy(parameters);

		for (final Target<?> target : argumentTargets()) {
			Object value;
			if (needDependency.contains(target))
				value = dependenciesInjector.provide(target);
			else
				value = argumentsInstantiator.instantiate(target, parameters);

			argumentValues.add(value);
		}

        Object newObjectValue = new Mirror().on(declaringClass()).invoke().constructor(constructor).withArgs(argumentValues.toArray());
        return new NewObject(argumentsInstantiator, this, newObjectValue);
	}

	public Collection<Target<?>> notFulfilledBy(final Parameters parameters) {
		final ArrayList<Target<?>> unfulfilledParameterTargets = new ArrayList<Target<?>>();

		for (final Target<?> parameterTarget : argumentTargets()) {
			if (!parameters.hasRelatedTo(parameterTarget))
				unfulfilledParameterTargets.add(parameterTarget);
		}
		return Collections.unmodifiableList(unfulfilledParameterTargets);
	}

	private List<Target<?>> argumentTargets() {
		final Iterable<Type> types = Arrays.asList(constructor.getGenericParameterTypes());

        final ArrayList<Target<?>> targets = Lists.newArrayList();
		for (Entry<Type, String> parameter : zip(types, names)) {
			targets.add(new Target<Object>(parameter.getKey(), parameter.getValue()));
		}

		return Collections.unmodifiableList(targets);
	}

	private Class<?> declaringClass() {
		return constructor.getDeclaringClass();
	}

	@Override
	public String toString() {
		return "(" + Joiner.on(", ").join(names) + ")";
	}

    public boolean canInstantiateOrInject(final Parameters relevantParameters, DependenciesInjector dependenciesInjector) {
		final Collection<Target<?>> uninstantiableByParameters = notFulfilledBy(relevantParameters);
		final boolean canObtainDependencies = dependenciesInjector.canObtainDependenciesFor(uninstantiableByParameters);
		return canObtainDependencies;
	}
}
