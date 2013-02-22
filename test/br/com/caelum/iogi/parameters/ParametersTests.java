package br.com.caelum.iogi.parameters;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import br.com.caelum.iogi.fixtures.TwoArguments;
import br.com.caelum.iogi.reflection.ClassConstructor;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.ParameterNamesProvider;

import com.google.common.collect.Lists;

public class ParametersTests {
	private final Mockery context = new Mockery();	

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
	public void namedAfterWillReturnAnEmptyParameterIfThereIsNoParameterBegginingWithTheTargetName() throws Exception {
		final Target<Object> target = Target.create(Object.class, "name"); 
		final Parameter one = new Parameter("fizzble", "one");
		final Parameter two = new Parameter("foozble", "two");
		
		final Parameters parameters = new Parameters(one, two);
		final Parameter found = parameters.namedAfter(target);
		assertEquals("", found.getValue());
	}
	
	@Test
	public void parametersNotUsedByAConstructorAreThoseWhoseFirstComponentsDontMatchWithTheConstructorArgumentNames() throws Exception {
		final Parameters parameters = parametersNamed("foo", "one", "two", "fizzle"); 
		final Constructor<TwoArguments> constructorWithParametersNamedOneAndTwo = TwoArguments.class.getDeclaredConstructor(int.class, int.class);
		final ClassConstructor constructor = new ClassConstructor(constructorWithParametersNamedOneAndTwo, providingNames("one", "two"));
		
		final Parameters notUsed = parameters.notUsedBy(constructor);
		assertThat(notUsed.getParametersList(), containsInAnyOrder(new Parameter("foo", ""), new Parameter("fizzle", "")));
	}
	
	@Test
	public void parametersForTargetAreThoseWhoseFirstNameComponentMatchTheTargetName() throws Exception {
		Parameter matchingParameter = new Parameter("matchingName.foo", "42");
		Parameter notMatchingParameter = new Parameter("notMatching.foo", "123");
		Parameters parameters = new Parameters(matchingParameter, notMatchingParameter);
		
		List<Parameter> obtained = parameters.forTarget(Target.create(Object.class, "matchingName"));
		assertEquals(asList(matchingParameter), obtained);
	}
	
	@Test
	public void ifThereAreNoMatchingParametersThenParametersForTargetReturnsAnEmptyList() throws Exception {
		Parameter notMatchingParameter = new Parameter("notMatching.foo", "123");
		Parameters parameters = new Parameters(notMatchingParameter);
		
		List<Parameter> obtained = parameters.forTarget(Target.create(Object.class, "matchingName"));
		assertTrue(obtained.isEmpty());
	}
	
	@Test
	public void parametersHaveParametersRelatedToATargetWhenAtLeastOneParameterHasTheTargetNameAsItsFirstSegment() throws Exception {
		Parameters parameters = parametersNamed("name.foo");
		
		assertTrue(parameters.hasRelatedTo(Target.create(Object.class, "name")));
		assertFalse(parameters.hasRelatedTo(Target.create(Object.class, "other")));
	}
	
    @Test
	public void foo() {
        Parameter p0 = new Parameter("foo[1]", "10");
        Parameter p1 = new Parameter("foo[0]", "00");
        Parameter p2 = new Parameter("foo[2]", "20");
        Parameter p3 = new Parameter("bar", "99");

        Parameters parameters = new Parameters(p0, p1, p2, p3);
        List<Parameter> result = parameters.forTarget(Target.create(Object.class, "foo"));
        assertEquals(result.get(0), p1);
        assertEquals(result.get(1), p0);
        assertEquals(result.get(2), p2);
	}
	
	private Parameters parametersNamed(final String... names) {
		final List<Parameter> params = Lists.newArrayList();
		for (final String name : names) {
			params.add(new Parameter(name, ""));
		}
		return new Parameters(params);
	}
	
	private ParameterNamesProvider providingNames(final String... names) {
		final ParameterNamesProvider mockParameterNamesProvider = context.mock(ParameterNamesProvider.class);

		context.checking(new Expectations() {
			{
				allowing(mockParameterNamesProvider).lookupParameterNames(with(any(AccessibleObject.class)));
				will(returnValue(Arrays.asList(names)));
			}
		});
		
		return mockParameterNamesProvider;
	}
}
