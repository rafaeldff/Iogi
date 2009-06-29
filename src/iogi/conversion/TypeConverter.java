package iogi.conversion;

import iogi.Instantiator;
import iogi.exceptions.ConversionException;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

public abstract class TypeConverter<T> implements Instantiator<T> {

	protected abstract T convert(String stringValue, Target<?> to) throws Exception;
	
	@Override
	public final T instantiate(final Target<?> target, final Parameters parameters) {
		final String stringValue = parameters.namedAfter(target).getValue();
		try {
			return convert(stringValue, target);
		} 
		catch (final ConversionException e) {
			throw e;
		} 
		catch (final Exception e) {
			throw new ConversionException(e, "Exception when trying to convert '%s' to a '%s' named '%s'", stringValue, target.getType(), target.getName());
		}
	}

}
