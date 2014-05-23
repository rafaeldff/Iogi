package br.com.caelum.iogi.reflection;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.List;

import br.com.caelum.iogi.spi.ParameterNamesProvider;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;

public class ParanamerParameterNamesProvider implements ParameterNamesProvider {
	private final static CachingParanamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());
	
	public List<String> lookupParameterNames(final AccessibleObject methodOrConstructor) {;
		return Arrays.asList(paranamer.lookupParameterNames(methodOrConstructor));
	}
}
