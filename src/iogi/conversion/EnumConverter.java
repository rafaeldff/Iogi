package iogi.conversion;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

public class EnumConverter<T extends Enum<T>> implements Instantiator<T> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return Enum.class.isAssignableFrom(target.getClassType());
	}
	
	@Override
	public T instantiate(Target<?> target, Parameters parameters) {
		Class<T> enumClass = enumClass((Class<?>) target.getClassType());
		return Enum.valueOf(enumClass, parameters.namedAfter(target).getValue());
	}

	@SuppressWarnings("unchecked")
	private Class<T> enumClass(Class<?> targetClass) {
		if (!Enum.class.isAssignableFrom(targetClass))
			throw new IllegalArgumentException();
		return (Class<T>)targetClass;
	}
}
