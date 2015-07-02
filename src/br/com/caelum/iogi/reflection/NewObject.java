package br.com.caelum.iogi.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.Matcher;
import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameters;

public class NewObject {
    public static NewObject nullNewObject() {
        return new NewObject(null, null, null) {
            @Override
            public Object value() {
                return null;
            }

            @Override
            public Object valueWithPropertiesSet() {
                return null;
            }
        };
    }

    private final Instantiator<?> propertiesInstantiator;
    private final Parameters parameters;
    private final Object object;

    public NewObject(final Instantiator<?> propertiesInstantiator, final Parameters parameters, final Object newObjectValue) {
        this.propertiesInstantiator = propertiesInstantiator;
        this.parameters = parameters;
        this.object = newObjectValue;
    }

    public Object valueWithPropertiesSet() {
        populateProperties();
        return value();
    }

    public Object value() {
        return object;
    }

    private void populateProperties() {
        for (final Setter setter : Setter.settersIn(object)) {
            setProperty(setter);
        }
	}

    private void setProperty(final Setter setter) {
        if (parameters.hasRelatedTo(setter.asTarget())) {
            final Object propertyValue = propertiesInstantiator.instantiate(setter.asTarget(), parameters);
            setter.set(propertyValue);
        }
    }

    private static abstract class Setter {
        private static Collection<Setter> settersIn(final Object object) {

            final ArrayList<Setter> setters = new ArrayList<Setter>();
            for (final Method setterMethod: JavaSetter.settersOf(object)) {
                setters.add(new JavaSetter(setterMethod, object));
            }
            for (final Method setterMethod: ScalaSetter.settersOf(object)) {
            	setters.add(new ScalaSetter(setterMethod, object));
            }
            
            return Collections.unmodifiableList(setters);
        }

        protected final Method setter;

        private final Object object;

        public Setter(final Method setter, final Object object) {
			this.setter = setter;
			this.object = object;
		}

		public void set(final Object argument) {
			new Mirror().on(object).invoke().method(setter).withArgs(argument);
		}

        private Target<Object> asTarget() {
            return new Target<Object>(type(), propertyName());
        }

        protected abstract String propertyName();

        private Type type() {
        	if(setter.getGenericParameterTypes().length == 0) {
        		return null;
        	}
            return setter.getGenericParameterTypes()[0];
        }

        @Override
        public String toString() {
            return "Setter(" + propertyName() +")";
        }
    }
    private static class JavaSetter extends Setter {
		public JavaSetter(Method setter, Object object) {
			super(setter, object);
		}

		@Override
		protected String propertyName() {
            final String capitalizedPropertyName = setter.getName().substring(3);
            final String propertyName = capitalizedPropertyName.substring(0, 1).toLowerCase() + capitalizedPropertyName.substring(1);
            return propertyName;
        }

		static List<Method> settersOf(Object object) {
			return new Mirror().on(object.getClass()).reflectAll().methods().matching(new Matcher<Method>() {
	            public boolean accepts(final Method method) {
	            	return !method.isBridge() && !method.isSynthetic()
	                		&& !Modifier.isAbstract(method.getModifiers())
	                		&& method.getName().startsWith("set");
	            }
	        });
		}
    }
    private static class ScalaSetter extends Setter {
    	public ScalaSetter(Method setter, Object object) {
    		super(setter, object);
    	}

    	@Override
    	protected String propertyName() {
    		return setter.getName().replace("_$eq", "");
    	}

    	static List<Method> settersOf(Object object) {
    		return new Mirror().on(object.getClass()).reflectAll().methods().matching(new Matcher<Method>() {
    			public boolean accepts(final Method method) {
    				return method.getName().endsWith("_$eq");
    			}
    		});
    	}
    }
}
