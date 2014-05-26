package br.com.caelum.iogi.spi;

import java.lang.reflect.AccessibleObject;
import java.util.List;

public interface ParameterNamesProvider {
	public abstract List<String> lookupParameterNames(final AccessibleObject methodOrConstructor);
}