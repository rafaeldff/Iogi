package iogi.conversion;

import iogi.reflection.Target;

public class EnumConverter extends TypeConverter<Object> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return Enum.class.isAssignableFrom(target.getClassType());
	}
	
	@Override
	protected Object convert(String stringValue, Target<?> to) {
		Class<?> enumClass = to.getClassType();
		ensureTargetIsAnEnum(to);
		
		if (isNumber(stringValue))
			return instantiateFromOrdinal(enumClass, stringValue);
		else
			return instantiateFromName(enumClass, stringValue);
	}

	private void ensureTargetIsAnEnum(Target<?> to) {
		if (!Enum.class.isAssignableFrom(to.getClassType()))
			throw new IllegalArgumentException();
	}

	private boolean isNumber(String stringValue) {
		for (int i = 0; i < stringValue.length(); i++)
			if (!Character.isDigit(stringValue.charAt(i)))
				return false;
		return true;
	}
	
	private Object instantiateFromOrdinal(Class<?> enumClass, String ordinalAsString) {
		try {
			Object[] enumConstants = enumClass.getEnumConstants();
			int ordinal = Integer.parseInt(ordinalAsString);
			return enumConstants[ordinal];
		} catch (Exception e) {
			throw new ConversionException("Failed to interpret '%s' as an ordinal index into enum '%s'",
					ordinalAsString, enumClass.getName());
		}
	}
	
	@SuppressWarnings("unchecked") //SupressWarnings ok becuase at this point we know type is an Enum
	private Object instantiateFromName(Class<?> type, String name) {
		try {
			Class<? extends Enum> enumClass = (Class<? extends Enum>) type;
			return Enum.valueOf(enumClass, name);
		} catch (IllegalArgumentException iae) {
			throw new ConversionException("Attempted to convert '%s' to an enum value of type '%s'",
					name, type.getName());
		}
	}
}
