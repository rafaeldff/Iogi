package iogi.reflection;


import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import iogi.Instantiator;
import iogi.conversion.StringConverter;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;

import java.lang.reflect.Constructor;
import java.util.HashSet;

import org.junit.Test;

import com.google.common.collect.ImmutableList;


public class ClassConstructorTest {
	private Constructor<Foo> fooConstructor;
	private Instantiator<?> primitiveInstantiator;
	
	public ClassConstructorTest() throws SecurityException, NoSuchMethodException {
		fooConstructor = Foo.class.getConstructor(String.class, String.class);
		primitiveInstantiator = new StringConverter();		
	}
	
	@Test
	public void twoClassConstructorsWithTheSameSetOfNamesAreEqual() throws Exception {
		HashSet<String> set1 = new HashSet<String>(asList("foo", "bar", "baz"));
		HashSet<String> set2 = new HashSet<String>(asList("foo", "bar", "baz"));
		
		ClassConstructor classConstructor1 = new ClassConstructor(set1);
		ClassConstructor classConstructor2 = new ClassConstructor(set2);
		
		assertEquals(classConstructor1, classConstructor2);
	}
	
	@Test
	public void twoClassConstructorsWithTheSameSetOfNamesHaveTheSameHashcode() throws Exception {
		HashSet<String> set1 = new HashSet<String>(asList("foo", "bar", "baz"));
		HashSet<String> set2 = new HashSet<String>(asList("foo", "bar", "baz"));
		
		ClassConstructor classConstructor1 = new ClassConstructor(set1);
		ClassConstructor classConstructor2 = new ClassConstructor(set2);
		
		assertEquals(classConstructor1.hashCode(), classConstructor2.hashCode());
	}
	
	@Test
	public void canCreateAClassConstructorFromAConstructor() throws Exception {
		HashSet<String> parameterNames = new HashSet<String>(asList("one", "two"));
		ClassConstructor fromNames = new ClassConstructor(parameterNames);
		ClassConstructor fromConstructor = new ClassConstructor(fooConstructor);
		
		assertEquals(fromNames, fromConstructor);
	}
	
	@Test
	public void canInstantiateFromArgumentNames() throws Exception {
		ClassConstructor constructor = new ClassConstructor(fooConstructor); 
		ImmutableList<Parameter> parameters = ImmutableList.<Parameter>builder().add(new Parameter("two",  "b")).add(new Parameter("one", "a")).build();
		Foo foo = (Foo)constructor.instantiate(primitiveInstantiator, new Parameters(parameters));
		assertEquals("a", foo.getOne());
		assertEquals("b", foo.getTwo());
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
