package iogi.collections;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;
import iogi.Iogi;
import iogi.NullDependencyProvider;
import iogi.collections.CyclingListInstantiatonTests.ContainsParameterizedList;
import iogi.parameters.Parameter;
import iogi.reflection.Target;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.junit.Test;


public class IndexedListInstantiatorTests {
	private final Iogi iogi = new Iogi(new NullDependencyProvider());
	
	@Test
	public void canInstantiateAListOfStrings() throws Exception {
		final Type parameterizedListType = ContainsParameterizedList.class.getDeclaredField("listOfInteger").getGenericType();
		final Target<List<Integer>> target = new Target<List<Integer>>(parameterizedListType, "list");
		
		final List<Integer> list = iogi.instantiate(target, new Parameter("list[0]", "1"), new Parameter("list[1]", "2"));
		assertThat(list, contains(1, 2));
	}
	
	@Test
	public void canInstantiateAListOfStringsAtSpacedIntervals() throws Exception {
		final Type parameterizedListType = ContainsParameterizedList.class.getDeclaredField("listOfInteger").getGenericType();
		final Target<List<Integer>> target = new Target<List<Integer>>(parameterizedListType, "list");
		
		final Parameter parameterAtIndex0 = new Parameter("list[0]", "42");
		final Parameter parameterAtIndex10 = new Parameter("list[10]", "666");
		final List<Integer> list = iogi.instantiate(target, parameterAtIndex0, parameterAtIndex10);
		assertThat(list, contains(42, 666));
	}
	
	@Test
	public void willInstantiateAnEmptyListIfGivenNoRelevantParameters() throws Exception {
		final Type parameterizedListType = ContainsParameterizedList.class.getDeclaredField("listOfInteger").getGenericType();
		final Target<List<Object>> target = new Target<List<Object>>(parameterizedListType, "list");
		
		final Collection<Object> list = iogi.instantiate(target);
		assertThat(list, is(empty()));
	}
}
