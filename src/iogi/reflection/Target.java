package iogi.reflection;


import iogi.DependenciesInjector;
import iogi.parameters.Parameters;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

public class Target<T> {
	public static <T> Target<T> create(final Class<T> type, final String name) {
		return new Target<T>(type, name);
	}

	private final Type type;
	private final String name;
	
	public Target(final Type type, final String name) {
		this.type = type;
		this.name = name;
	}

	public Class<T> getClassType() {
		return findRawClassType(type);
	}
	
	@SuppressWarnings("unchecked")
	private Class<T> findRawClassType(final Type type) {
		if (type instanceof ParameterizedType)
			return findRawClassType(((ParameterizedType)type).getRawType());
		else
			return (Class<T>)type;
	}
	
	public String getName() {
		return name;
	} 

	@SuppressWarnings("unchecked")
	public boolean isPrimitiveLike() {
		if (!(type instanceof Class))
			return false;
		
		return Primitives.isPrimitiveLike(getClassType()) || getClassType() == String.class; 
	}
	
	public T cast(final Object object) {
		if (getClassType().isPrimitive())
			return Primitives.primitiveCast(object, getClassType());
			
		return getClassType().cast(object);

	}
	
	public Set<ClassConstructor> classConstructors() {
		final HashSet<ClassConstructor> classConstructors = new HashSet<ClassConstructor>();
		for (final Constructor<?> constructor : getClassType().getConstructors()) {
			classConstructors.add(new ClassConstructor(constructor));
		}
		return classConstructors;
	}

	 public boolean isInstantiable() {
		return !getClassType().isInterface() && !Modifier.isAbstract(getClassType().getModifiers()) && getClassType() != Void.class;
	}
	 
	 public Type getType() {
		return type;
	}

	public Target<Object> typeArgument(final int index) {
		final ParameterizedType thisAsParameterizedType = (ParameterizedType)this.getType();
		final Type[] typeArguments = thisAsParameterizedType.getActualTypeArguments();
		final Type typeArgument = typeArguments[index];
		return new Target<Object>(typeArgument, this.getName());
	}

	public Class<?> arrayElementType() {
		return getClassType().getComponentType();
	}
	
	public Target<?> arrayElementTarget() {
		return Target.create(arrayElementType(), getName());
	}

	public Collection<ClassConstructor> compatibleConstructors(final Parameters relevantParameters, final DependenciesInjector dependenciesInjector) {
		final LinkedList<ClassConstructor> compatible = new LinkedList<ClassConstructor>();
		
		for (final ClassConstructor classConstructor : classConstructors()) {
			if (canInstantiateOrInject(classConstructor, relevantParameters, dependenciesInjector))
				compatible.add(classConstructor);
		}
		
		return compatible;
	}

	private boolean canInstantiateOrInject(final ClassConstructor classConstructor, final Parameters relevantParameters,
			final DependenciesInjector dependenciesInjector) {
		final Collection<Target<?>> uninstatiableByParameters = classConstructor.notFulfilledBy(relevantParameters);
		final boolean canObtainDependencies = dependenciesInjector.canObtainDependenciesFor(uninstatiableByParameters);
		return canObtainDependencies;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Target<?> other = (Target<?>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Target(name=%s, type=%s)", name, getClassType());
	}
	
	private static class Primitives {
		private static final Map<Class<?>, Class<?>> primitiveToObject = ImmutableMap.<Class<?>, Class<?>>builder()
			.put(Boolean.TYPE, Boolean.class)
			.put(Character.TYPE, Character.class)
			.put(Byte.TYPE, Byte.class)
			.put(Short.TYPE, Short.class)
			.put(Integer.TYPE, Integer.class)
			.put(Long.TYPE, Long.class)
			.put(Float.TYPE, Float.class)
			.put(Double.TYPE, Double.class)
			.put(Void.TYPE, Void.class)
			.build();

		private Primitives() {}

		/**
		 * @param type
		 * @return true iff type represents a primitive (like int.class) or a primitive wrapper (like Integer.class)
		 */
		public static boolean isPrimitiveLike(final Class<?> type) {
			return primitiveToObject.keySet().contains(type) || primitiveToObject.values().contains(type);
		}

		@SuppressWarnings("unchecked")
		public static <T> T primitiveCast(final Object object, final Class<T> type) {
			return (T) primitiveToObject.get(type).cast(object);
		}
		
	}
}