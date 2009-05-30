package iogi.reflection;


import static org.junit.Assert.assertEquals;
import iogi.Instantiator;
import iogi.NullDependencyProvider;
import iogi.conversion.StringConverter;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.junit.Test;

import com.google.common.collect.ImmutableList;


public class ClassConstructorTests {
	private Constructor<Foo> fooConstructor;
	private Instantiator<?> primitiveInstantiator;
	
	public ClassConstructorTests() throws SecurityException, NoSuchMethodException {
		fooConstructor = Foo.class.getConstructor(String.class, String.class);
		primitiveInstantiator = new StringConverter();		
	}
	
	@Test
	public void canInstantiateFromArgumentNames() throws Exception {
		ClassConstructor constructor = new ClassConstructor(fooConstructor); 
		ImmutableList<Parameter> parameters = ImmutableList.<Parameter>builder().add(new Parameter("two",  "b")).add(new Parameter("one", "a")).build();
		Foo foo = (Foo)constructor.instantiate(primitiveInstantiator, new Parameters(parameters), new NullDependencyProvider());
		assertEquals("a", foo.getOne());
		assertEquals("b", foo.getTwo());
	}
	
	@Test
	public void sizeIsTheNumberOfArguments() throws Exception {
		ClassConstructor constructor = new ClassConstructor(fooConstructor); 
		assertEquals(2, constructor.size());
	}
	
	@Test
	public void notFulfilledByParametersWillReturnTargetsForParametersWhoseNamesArentFoundInTheParameters() throws Exception {
		ClassConstructor constructor = new ClassConstructor(fooConstructor); 
		Parameter aParameter = new Parameter("two", "");
		
		Collection<Target<?>> unfulfilled = constructor.notFulfilledBy(new Parameters(aParameter));
		
		assertEquals(1, unfulfilled.size());
		assertEquals(Target.create(String.class, "one"), unfulfilled.iterator().next());
	}
	
	public static class Foo {
		private final String one;
		private final String two;

		public Foo(String one, String two) {
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
