package br.com.caelum.iogi;

import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

public interface Instantiator<T> {
	public boolean isAbleToInstantiate(Target<?> target, Parameters parameters);
	public T instantiate(Target<?> target, Parameters parameters);
}
