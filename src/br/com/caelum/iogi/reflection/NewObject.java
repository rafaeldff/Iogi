package br.com.caelum.iogi.reflection;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameters;
import net.vidageek.mirror.dsl.Matcher;
import net.vidageek.mirror.dsl.Mirror;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class NewObject {
    private Instantiator<?> propertiesInstantiator;
    private Object value;
    private ClassConstructor constructorUsed;

    public NewObject(Instantiator<?> instantiator, ClassConstructor constructorUsed, Object value) {
        propertiesInstantiator = instantiator;
        this.value = value;
        this.constructorUsed = constructorUsed;
    }

    public Object value() {
        return value;
    }

    public void populateRemainingProperties(final Parameters parameters) {
        if (value == null)
            return;
        final Parameters remainingParameters = parameters.notUsedBy(constructorUsed);
		for (final Setter setter : settersIn(value)) {
			final Target<?> target = new Target<Object>(setter.type(), setter.propertyName());
			if (remainingParameters.hasRelatedTo(target)) {
				final Object argument = propertiesInstantiator.instantiate(target, parameters);
				setter.set(argument);
			}
		}
	}
    
    private Collection<Setter> settersIn(final Object object) {
		final ArrayList<Setter> foundSetters = new ArrayList<Setter>();
		for (final Method setterMethod: new Mirror().on(object.getClass()).reflectAll().methodsMatching(SETTERS)) {
			foundSetters.add(new Setter(setterMethod, object));
		}
		return Collections.unmodifiableList(foundSetters);
	}
	
	private static final Matcher<Method> SETTERS = new Matcher<Method>(){
		public boolean accepts(final Method method) {
			return method.getName().startsWith("set");
		}
	};

	private static class Setter {
		private final Method setter;
		private final Object object;
		
		public Setter(final Method setter, final Object object) {
			this.setter = setter;
			this.object = object;
		}
		
		public void set(final Object argument) {
			new Mirror().on(object).invoke().method(setter).withArgs(argument);
		}
		
		public String propertyName() {
			final String capitalizedPropertyName = setter.getName().substring(3);
			final String propertyName = capitalizedPropertyName.substring(0, 1).toLowerCase() + capitalizedPropertyName.substring(1);
			return propertyName;
		}
		
		public Type type() {
			return setter.getGenericParameterTypes()[0];
		}
		
		@Override
		public String toString() {
			return "Setter(" + propertyName() +")";
		}
	}
}
