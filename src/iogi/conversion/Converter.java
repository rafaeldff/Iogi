package iogi.conversion;


import iogi.Parameter;
import iogi.Parameters;
import iogi.Target;

import java.util.Collection;
import java.util.List;

public class Converter {
	public Collection<TypeConverter<?>> converters;

	public Converter(Collection<TypeConverter<?>> converters) {
		this.converters = converters;
	}

	public Object convert(String value, Target<?> formalParameterType, List<Parameter> parameters) {
		for (TypeConverter<?> typeConverter : converters) {
			if (typeConverter.isAbleToConvertTo(formalParameterType.getClassType()))
				return typeConverter.convert(value, formalParameterType, new Parameters(parameters));
		}
		throw new ConversionException("Cannot handle conversions to " + formalParameterType);
	}
	
	
}