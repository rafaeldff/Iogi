package iogi.conversion;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

public abstract class TypeConverter<T> implements Instantiator<T> {

	protected abstract T convert(String stringValue, Target<?> to);
	
	@Override
	public final T instantiate(Target<?> target, Parameters parameters) {
		String stringValue = parameters.namedAfter(target).getValue();
		try {
			return convert(stringValue, target);
		} 
		catch (ConversionException e) {
			throw e;
		} 
		catch (Exception e) {
			throw new ConversionException(e, "Exception when trying to convert '%s' to a '%s' named '%s'", stringValue, target.getType(), target.getName());
		}
	}

}
