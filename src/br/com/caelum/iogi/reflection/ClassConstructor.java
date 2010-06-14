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
    private final LinkedHashSet<String> names;
	private final Constructor<?> constructor;
	
	public ClassConstructor(final Constructor<?> constructor, final ParameterNamesProvider parameterNamesProvider) {
		this.constructor = constructor;
		this.names = Sets.newLinkedHashSet(parameterNamesProvider.lookupParameterNames(constructor));
	}
	
	public Set<String> getNames() {
		return names;
	}
	
	public int size() {
		return names.size();
	}

	public NewObject instantiate(final Instantiator<?> instantiator, final Parameters parameters, final DependenciesInjector dependenciesInjector) {
		final List<Object> argumentValues = Lists.newArrayListWithCapacity(size());
		final Collection<Target<?>> needDependency = notFulfilledBy(parameters);
		
		for (final Target<?> target : argumentTargets()) {
			Object value;
			if (needDependency.contains(target))
				value = dependenciesInjector.provide(target);
			else
				value = instantiator.instantiate(target, parameters);
			
			argumentValues.add(value);
		}

        Object newObjectValue = new Mirror().on(declaringClass()).invoke().constructor(constructor).withArgs(argumentValues.toArray());
        return new NewObject(instantiator, this, newObjectValue);
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
}
