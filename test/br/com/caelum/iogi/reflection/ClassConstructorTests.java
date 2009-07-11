package br.com.caelum.iogi.reflection;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import br.com.caelum.iogi.DependenciesInjector;
import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.conversion.StringConverter;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.spi.ParameterNamesProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;

import com.google.common.collect.ImmutableList;

public class ClassConstructorTests {
	private final Constructor<Foo> fooConstructor;
	private final Instantiator<?> primitiveInstantiator;
	private final Mockery context; 
	
	public ClassConstructorTests() throws SecurityException, NoSuchMethodException {
		fooConstructor = Foo.class.getConstructor(String.class, String.class);
		primitiveInstantiator = new StringConverter();		
		context = new Mockery();
	}
	
	@Test
	public void canInstantiateFromArgumentNames() throws Exception {
		final ClassConstructor constructor = new ClassConstructor(fooConstructor, providingNames("one", "two")); 
		final ImmutableList<Parameter> parameters = ImmutableList.<Parameter>builder().add(new Parameter("two",  "b")).add(new Parameter("one", "a")).build();
		final DependenciesInjector nullDependenciesInjector = new DependenciesInjector(new NullDependencyProvider());
		final Foo foo = (Foo)constructor.instantiate(primitiveInstantiator, new Parameters(parameters), nullDependenciesInjector);
		assertEquals("a", foo.getOne());
		assertEquals("b", foo.getTwo());
	}
	
	@Test
	public void sizeIsTheNumberOfArguments() throws Exception {
		final ClassConstructor constructor = new ClassConstructor(fooConstructor, providingNames("one", "two")); 
		assertEquals(2, constructor.size());
	}
	
	@Test
	public void notFulfilledByParametersWillReturnTargetsForParametersWhoseNamesArentFoundInTheParameters() throws Exception {
		final ClassConstructor constructor = new ClassConstructor(fooConstructor, providingNames("one", "two")); 
		final Parameter aParameter = new Parameter("two", "");
		
		final Collection<Target<?>> unfulfilled = constructor.notFulfilledBy(new Parameters(aParameter));
		
		assertEquals(1, unfulfilled.size());
		assertEquals(Target.create(String.class, "one"), unfulfilled.iterator().next());
	}
	
	private ParameterNamesProvider providingNames(final String... names) {
		final ParameterNamesProvider mockParameterNamesProvider = context.mock(ParameterNamesProvider.class);

		context.checking(new Expectations() {{
			allowing(mockParameterNamesProvider).lookupParameterNames(with(any(AccessibleObject.class)));
			will(returnValue(Arrays.asList(names)));
		}});
		
		return mockParameterNamesProvider;
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
