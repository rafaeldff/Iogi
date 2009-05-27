package iogi.parameters;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import iogi.reflection.ClassConstructor;
import iogi.reflection.Target;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ParametersTests {
	
	@Test
	public void namedAfterReturnsAParameterWhoseFirstComponentIsTheSameAsTheTargetName() throws Exception {
		Target<Object> target = Target.create(Object.class, "name"); 
		Parameter correct = new Parameter("name.asdf", "");
		Parameter incorrect = new Parameter("otherName.asdf", "");
		
		Parameters parameters = new Parameters(correct, incorrect);
		assertEquals(correct, parameters.namedAfter(target));
	}
	
	@Test
	public void ifThereIsAParameterWithTheSameWholeNameAsTheTargetNameThenItWillBeReturnedByNamedAfter() throws Exception {
		Target<Object> target = Target.create(Object.class, "name"); 
		Parameter correct = new Parameter("name", "");
		Parameter incorrect = new Parameter("otherName", "");
		
		Parameters parameters = new Parameters(correct, incorrect);
		assertEquals(correct, parameters.namedAfter(target));
	}
	
	@Test(expected=IllegalStateException.class)
	public void namedAfterWillThrowAnExceptionIfThereIsMoreThanOneParameterBegginingWithTheTargetName() throws Exception {
		Target<Object> target = Target.create(Object.class, "name"); 
		Parameter one = new Parameter("name", "one");
		Parameter two = new Parameter("name", "two");
		
		Parameters parameters = new Parameters(one, two);
		parameters.namedAfter(target);
	}
	
	@Test
	public void namedAfterWillReturnNullIfThereIsNoParameterBegginingWithTheTargetName() throws Exception {
		Target<Object> target = Target.create(Object.class, "name"); 
		Parameter one = new Parameter("fizzble", "one");
		Parameter two = new Parameter("foozble", "two");
		
		Parameters parameters = new Parameters(one, two);
		assertNull(parameters.namedAfter(target));
	}
	
	@Test
	public void compatibleClassConstructorsAreThoseWhoseParameterNamesAreASubset() throws Exception {
		Parameters parameters = parametersNamed("a", "b", "c"); 
		ClassConstructor subset = new ClassConstructor(Sets.newHashSet("a", "b"));
		ClassConstructor alsoASubset = new ClassConstructor(Sets.newHashSet("b"));
		ClassConstructor notASubset = new ClassConstructor(Sets.newHashSet("a", "b", "x"));
		
		Set<ClassConstructor> candidates = Sets.newHashSet(subset, notASubset, alsoASubset);
		Collection<ClassConstructor> compatible = parameters.compatible(candidates);
		
		assertEquals(2, compatible.size());
		assertThat(compatible, containsInAnyOrder(subset, alsoASubset));
	}
	
	@Test
	public void compatibleWillConsiderIdenticalParameterListsAsSubsets() throws Exception {
		Parameters parameters = parametersNamed("a", "b", "c"); 
		ClassConstructor subset = new ClassConstructor(Sets.newHashSet("a", "b", "c"));
		ClassConstructor notASubset = new ClassConstructor(Sets.newHashSet("a", "b", "x"));
		
		Set<ClassConstructor> candidates = Sets.newHashSet(subset, notASubset);
		Collection<ClassConstructor> compatible = parameters.compatible(candidates);
		
		assertEquals(1, compatible.size());
		assertEquals(subset, compatible.iterator().next());
	}

	@Test
	public void parametersNotUsedByAConstructorAreThoseWhoseFirstComponentsDontMatchWithTheConstructorArgumentNames() throws Exception {
		Parameters parameters = parametersNamed("foo", "bar", "baz", "fizzle"); 
		ClassConstructor constructor = new ClassConstructor(Sets.newHashSet("bar", "baz"));
		
		Parameters notUsed = parameters.notUsedBy(constructor);
		assertEquals(parametersNamed("foo", "fizzle"), notUsed);
	}
	
	private Parameters parametersNamed(String... names) {
		List<Parameter> params = Lists.newArrayList();
		for (String name : names) {
			params.add(new Parameter(name, ""));
		}
		return new Parameters(params);
	}
}
