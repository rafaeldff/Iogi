package iogi.conversion;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

public class ByteConverter implements Instantiator<Byte> {
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == Byte.class || target.getClassType() == byte.class;
	}

	@Override
	public Byte instantiate(Target<?> target, Parameters parameters) {
		return Byte.valueOf(parameters.namedAfter(target).getValue());
	}
}