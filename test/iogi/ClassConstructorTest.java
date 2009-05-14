package iogi;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;


public class ClassConstructorTest {
	@Test
	public void twoClassConstructorsWithTheSameSetOfNamesAreEqual() throws Exception {
		HashSet<String> set1 = new HashSet<String>(asList("foo", "bar", "baz"));
		HashSet<String> set2 = new HashSet<String>(asList("foo", "bar", "baz"));
		
		ClassConstructor classConstructor1 = new ClassConstructor(set1);
		ClassConstructor classConstructor2 = new ClassConstructor(set2);
		
		assertEquals(classConstructor1, classConstructor2);
	}
	
	@Test
	public void canCreateAClassConstructorFromAConstructor() throws Exception {
		HashSet<String> parameterNames = new HashSet<String>(asList("one", "two"));
		ClassConstructor fromNames = new ClassConstructor(parameterNames);
		ClassConstructor fromConstructor = new ClassConstructor(Foo.class.getConstructor(String.class, String.class));
		
		assertEquals(fromNames, fromConstructor);
	}
	
	public static class Foo {
		public Foo(String one, String two) {}
	}
}
