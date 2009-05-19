package iogi.conversion;

import iogi.Instantiator;
import iogi.Parameters;
import iogi.Target;

public class StringConverter implements Instantiator<String> {

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType() == String.class;
	}
	
	@Override
	public String instantiate(Target<?> target, Parameters parameters) {
		return parameters.namedAfter(target).getValue();
	}
}
