package iogi;

import iogi.conversion.ConversionException;
import iogi.conversion.TypeConverter;

import java.util.Collection;

public class Converter {
	public Collection<TypeConverter<?>> converters;

	public Converter(Collection<TypeConverter<?>> converters) {
		this.converters = converters;
	}

	public Object convert(String value, Class<?> formalParameterType) {
		for (TypeConverter<?> typeConverter : converters) {
			if (typeConverter.isAbleToConvertTo(formalParameterType))
				return typeConverter.convert(value, formalParameterType);
		}
		throw new ConversionException("Cannot handle conversions to " + formalParameterType);
	}
	
	
}