package iogi.conversion;

import iogi.reflection.Target;

public class EnumConverter extends TypeConverter<Object> {
	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return Enum.class.isAssignableFrom(target.getClassType());
	}
	
	@Override
	protected Object convert(final String stringValue, final Target<?> to) {
		final Class<?> enumClass = to.getClassType();
		ensureTargetIsAnEnum(to);
		
		if (isNumber(stringValue))
			return instantiateFromOrdinal(enumClass, stringValue);
		else
			return instantiateFromName(enumClass, stringValue);
	}

	private void ensureTargetIsAnEnum(final Target<?> to) {
		if (!Enum.class.isAssignableFrom(to.getClassType()))
			throw new IllegalArgumentException();
	}

	private boolean isNumber(final String stringValue) {
		for (int i = 0; i < stringValue.length(); i++)
			if (!Character.isDigit(stringValue.charAt(i)))
				return false;
		return true;
	}
	
	private Object instantiateFromOrdinal(final Class<?> enumClass, final String ordinalAsString) {
		try {
			final Object[] enumConstants = enumClass.getEnumConstants();
			final int ordinal = Integer.parseInt(ordinalAsString);
			return enumConstants[ordinal];
		} catch (final Exception e) {
			throw new ConversionException("Failed to interpret '%s' as an ordinal index into enum '%s'",
					ordinalAsString, enumClass.getName());
		}
	}
	
	@SuppressWarnings("unchecked") //SupressWarnings ok becuase at this point we know type is an Enum
	private Object instantiateFromName(final Class<?> type, final String name) {
		try {
			final Class<? extends Enum> enumClass = (Class<? extends Enum>) type;
			return Enum.valueOf(enumClass, name);
		} catch (final IllegalArgumentException iae) {
			throw new ConversionException("Attempted to convert '%s' to an enum value of type '%s'",
					name, type.getName());
		}
	}
}
