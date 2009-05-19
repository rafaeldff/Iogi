package iogi;

import iogi.parameters.Parameters;
import iogi.reflection.Target;



public interface Instantiator<T> {
	public boolean isAbleToInstantiate(Target<?> target);
	public T instantiate(Target<?> target, Parameters parameters);
}
