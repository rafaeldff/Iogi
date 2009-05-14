package iogi;

import iogi.conversion.TypeConverter;

import java.util.List;

public class Converter {
	public List<TypeConverter<?>> converters;

	public Converter(List<TypeConverter<?>> converters) {
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