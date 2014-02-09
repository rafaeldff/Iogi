package br.com.caelum.iogi.conversion;

import br.com.caelum.iogi.exceptions.ConversionException;
import br.com.caelum.iogi.reflection.Target;

public class EnumConverter extends TypeConverter<Object> {
	public boolean isAbleToInstantiate(final Target<?> target) {
		return Enum.class.isAssignableFrom(target.getClassType());
	}
	
	@Override
	protected Object convert(final String stringValue, final Target<?> to) {
		ensureTargetIsAnEnum(to);
		
		final Class<?> enumClass = to.getClassType();
		if (isNumber(stringValue))
			return instantiateFromOrdinal(enumClass, stringValue, to);
		else
			return instantiateFromName(enumClass, stringValue, to);
	}

	private void ensureTargetIsAnEnum(final Target<?> to) {
		if (!Enum.class.isAssignableFrom(to.getClassType()))
			throw new ConversionException("Target %s does not represent a Java enum.", to);
	}

	private boolean isNumber(final String stringValue) {
		for (int i = 0; i < stringValue.length(); i++)
			if (!Character.isDigit(stringValue.charAt(i)))
				return false;
		return true;
	}
	
	private Object instantiateFromOrdinal(final Class<?> enumClass, final String ordinalAsString, final Target<?> to) {
		try {
			final Object[] enumConstants = enumClass.getEnumConstants();
			final int ordinal = Integer.parseInt(ordinalAsString);
			return enumConstants[ordinal];
		} catch (final Exception e) {
			throw new ConversionException("Failed to interpret '%s' as an ordinal index into enum '%s' " +
					"(when trying to fulfill target %s)",
					ordinalAsString, enumClass.getName(), to);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" }) //SupressWarnings ok because at this point we know type is an Enum
	private Object instantiateFromName(final Class<?> type, final String name, final Target<?> to) {
		try {
			final Class<? extends Enum> enumClass = (Class<? extends Enum>) type;
			return Enum.valueOf(enumClass, name);
		} catch (final IllegalArgumentException iae) {
			throw new ConversionException("Attempted to convert '%s' to an enum value of type '%s' " +
					"(when trying to fulfill target %s)",
					name, type.getName(), to);
		}
	}
}
