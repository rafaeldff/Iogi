package br.com.caelum.iogi.reflection;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.junit.Test;

import br.com.caelum.iogi.DependenciesInjector;
import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.conversion.StringConverter;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.util.NullDependencyProvider;

import com.google.common.collect.ImmutableList;

public class ClassConstructorTests {
	private final Constructor<Foo> fooConstructor;
	private final Instantiator<?> primitiveInstantiator;
	
	public ClassConstructorTests() throws SecurityException, NoSuchMethodException {
		fooConstructor = Foo.class.getConstructor(String.class, String.class);
		primitiveInstantiator = new StringConverter();		
	}
	
	@Test
	public void canInstantiateFromArgumentNames() throws Exception {
		final ClassConstructor constructor = new ClassConstructor(fooConstructor); 
		final ImmutableList<Parameter> parameters = ImmutableList.<Parameter>builder().add(new Parameter("two",  "b")).add(new Parameter("one", "a")).build();
		final DependenciesInjector nullDependenciesInjector = new DependenciesInjector(new NullDependencyProvider());
		final Foo foo = (Foo)constructor.instantiate(primitiveInstantiator, new Parameters(parameters), nullDependenciesInjector);
		assertEquals("a", foo.getOne());
		assertEquals("b", foo.getTwo());
	}
	
	@Test
	public void sizeIsTheNumberOfArguments() throws Exception {
		final ClassConstructor constructor = new ClassConstructor(fooConstructor); 
		assertEquals(2, constructor.size());
	}
	
	@Test
	public void notFulfilledByParametersWillReturnTargetsForParametersWhoseNamesArentFoundInTheParameters() throws Exception {
		final ClassConstructor constructor = new ClassConstructor(fooConstructor); 
		final Parameter aParameter = new Parameter("two", "");
		
		final Collection<Target<?>> unfulfilled = constructor.notFulfilledBy(new Parameters(aParameter));
		
		assertEquals(1, unfulfilled.size());
		assertEquals(Target.create(String.class, "one"), unfulfilled.iterator().next());
	}
	
	public static class Foo {
		private final String one;
		private final String two;

		public Foo(final String one, final String two) {
			this.one = one;
			this.two = two;
		}
		
		public String getOne() {
			return one;
		}
		
		public String getTwo() {
			return two;
		}
	}
}
