package iogi.conversion;

import iogi.Instantiatior;
import iogi.Parameters;
import iogi.Primitives;
import iogi.Target;


public class ObjectConverter implements TypeConverter<Object> {
	private final Instantiatior instantiator;

	public ObjectConverter(Instantiatior instantiator) {
		this.instantiator = instantiator;
	}
	
	@Override
	public boolean isAbleToConvertTo(Class<?> type) {
		return !Primitives.isPrimitiveLike(type);
	}

	@Override
	public Object convert(String stringRepresentation, Target<?> target, Parameters parameters) {
		return instantiator.instantiate(target, parameters);
	}

}
