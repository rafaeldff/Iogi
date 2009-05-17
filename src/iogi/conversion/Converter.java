package iogi.conversion;


import iogi.Target;

import java.util.Collection;
import java.util.Map;

public class Converter {
	public Collection<TypeConverter<?>> converters;

	public Converter(Collection<TypeConverter<?>> converters) {
		this.converters = converters;
	}

	public Object convert(String value, Target<?> formalParameterType, Map<String, String> arguments) {
		for (TypeConverter<?> typeConverter : converters) {
			if (typeConverter.isAbleToConvertTo(formalParameterType.getClassType()))
				return typeConverter.convert(value, formalParameterType, arguments);
		}
		throw new ConversionException("Cannot handle conversions to " + formalParameterType);
	}
	
	
}