package iogi.conversion;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

public abstract class TypeConverter<T> implements Instantiator<T> {

	public abstract T convert(String stringValue);
	
	@Override
	public final T instantiate(Target<?> target, Parameters parameters) {
		return convert(parameters.namedAfter(target).getValue());
	}

}
