package iogi.parameters;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import iogi.reflection.ClassConstructor;
import iogi.reflection.Target;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ParametersTests {
	
	@Test
	public void namedAfterReturnsAParameterWhoseFirstComponentIsTheSameAsTheTargetName() throws Exception {
		final Target<Object> target = Target.create(Object.class, "name"); 
		final Parameter correct = new Parameter("name.asdf", "");
		final Parameter incorrect = new Parameter("otherName.asdf", "");
		
		final Parameters parameters = new Parameters(correct, incorrect);
		assertEquals(correct, parameters.namedAfter(target));
	}
	
	@Test
	public void ifThereIsAParameterWithTheSameWholeNameAsTheTargetNameThenItWillBeReturnedByNamedAfter() throws Exception {
		final Target<Object> target = Target.create(Object.class, "name"); 
		final Parameter correct = new Parameter("name", "");
		final Parameter incorrect = new Parameter("otherName", "");
		
		final Parameters parameters = new Parameters(correct, incorrect);
		assertEquals(correct, parameters.namedAfter(target));
	}
	
	@Test(expected=IllegalStateException.class)
	public void namedAfterWillThrowAnExceptionIfThereIsMoreThanOneParameterBegginingWithTheTargetName() throws Exception {
		final Target<Object> target = Target.create(Object.class, "name"); 
		final Parameter one = new Parameter("name", "one");
		final Parameter two = new Parameter("name", "two");
		
		final Parameters parameters = new Parameters(one, two);
		parameters.namedAfter(target);
	}
	
	@Test
	public void namedAfterWillReturnNullIfThereIsNoParameterBegginingWithTheTargetName() throws Exception {
		final Target<Object> target = Target.create(Object.class, "name"); 
		final Parameter one = new Parameter("fizzble", "one");
		final Parameter two = new Parameter("foozble", "two");
		
		final Parameters parameters = new Parameters(one, two);
		assertNull(parameters.namedAfter(target));
	}
	
	@Test
	public void parametersNotUsedByAConstructorAreThoseWhoseFirstComponentsDontMatchWithTheConstructorArgumentNames() throws Exception {
		final Parameters parameters = parametersNamed("foo", "bar", "baz", "fizzle"); 
		final ClassConstructor constructor = new ClassConstructor(Sets.newHashSet("bar", "baz"));
		
		final Parameters notUsed = parameters.notUsedBy(constructor);
		assertThat(notUsed.getParametersList(), containsInAnyOrder(new Parameter("foo", ""), new Parameter("fizzle", "")));
	}
	
	private Parameters parametersNamed(final String... names) {
		final List<Parameter> params = Lists.newArrayList();
		for (final String name : names) {
			params.add(new Parameter(name, ""));
		}
		return new Parameters(params);
	}
}
