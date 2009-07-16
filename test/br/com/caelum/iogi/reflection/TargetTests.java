package br.com.caelum.iogi.reflection;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import br.com.caelum.iogi.fixtures.OnlyOneProtectedConstructor;

public class TargetTests {
	@Test
	public void willListProtectedConstructors() {
		final Target<OnlyOneProtectedConstructor> target = Target.create(OnlyOneProtectedConstructor.class, "foo");
		final Set<ClassConstructor> classConstructors = target.classConstructors(new ParanamerParameterNamesProvider());
		assertEquals(1, classConstructors.size());
	}
}
