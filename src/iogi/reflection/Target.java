package iogi.reflection;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class Target<T> {
	public static <T> Target<T> create(Class<T> type, String name) {
		return new Target<T>(type, name);
	}

	private final Type type;
	private final String name;
	
	public Target(Type type, String name) {
		this.type = type;
		this.name = name;
	}

	public Class<T> getClassType() {
		return findRawClassType(type);
	}
	
	@SuppressWarnings("unchecked")
	private Class<T> findRawClassType(Type type) {
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
	
	public T cast(Object object) {
		if (getClassType().isPrimitive())
			return Primitives.primitiveCast(object, getClassType());
			
		return getClassType().cast(object);

	}
	
	@Override
	public String toString() {
		return String.format("Target(name=%s, type=%s)", name, getClassType());
	}
	
	public Set<ClassConstructor> classConstructors() {
		HashSet<ClassConstructor> classConstructors = new HashSet<ClassConstructor>();
		for (Constructor<?> constructor : getClassType().getConstructors()) {
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

	public Target<Object> typeArgument(int index) {
		ParameterizedType thisAsParameterizedType = (ParameterizedType)this.getType();
		Type[] typeArguments = thisAsParameterizedType.getActualTypeArguments();
		Type typeArgument = typeArguments[index];
		return new Target<Object>(typeArgument, this.getName());
	}
}