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
		Class<T> enumClass = enumClass(target.getClassType());
		String stringValue = parameters.namedAfter(target).getValue();
		
		if (isNumber(stringValue))
			return instantiateFromOrdinal(enumClass, stringValue);
		else
			return instantiateFromName(enumClass, stringValue);
	}

	@SuppressWarnings("unchecked")
	private Class<T> enumClass(Class<?> targetClass) {
		if (!Enum.class.isAssignableFrom(targetClass))
			throw new IllegalArgumentException();
		return (Class<T>)targetClass;
	}

	private boolean isNumber(String stringValue) {
		for (int i = 0; i < stringValue.length(); i++)
			if (!Character.isDigit(stringValue.charAt(i)))
				return false;
		return true;
	}
	
	private T instantiateFromOrdinal(Class<T> enumClass, String ordinalAsString) {
		try {
			T[] enumConstants = enumClass.getEnumConstants();
			int ordinal = Integer.parseInt(ordinalAsString);
			return enumConstants[ordinal];
		} catch (Exception e) {
			throw new ConversionException("Failed to interpret '%s' as an ordinal index into enum '%s'",
					ordinalAsString, enumClass.getName());
		}
	}
	
	private T instantiateFromName(Class<T> enumClass, String name) {
		try {
			return Enum.valueOf(enumClass, name);
		} catch (IllegalArgumentException iae) {
			throw new ConversionException("Attempted to convert '%s' to an enum value of type '%s'",
					name, enumClass.getName());
		}
	}
}
