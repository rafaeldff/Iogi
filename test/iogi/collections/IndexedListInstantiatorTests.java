package iogi.collections;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import iogi.Iogi;
import iogi.NullDependencyProvider;
import iogi.exceptions.InvalidTypeException;
import iogi.fixtures.ContainsAParameterizedCollection;
import iogi.fixtures.ContainsParameterizedList;
import iogi.fixtures.OneString;
import iogi.fixtures.TwoArguments;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.lang.reflect.Type;
import java.util.Arrays;
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
	
	@Test
	public void canInstantiateAListOfObjects() throws Exception {
		final Type type = ContainsParameterizedList.class.getDeclaredField("listOfOneString").getGenericType();
		final Target<List<OneString>> target = new Target<List<OneString>>(type, "list");
		final List<OneString> list = iogi.instantiate(target, new Parameter("list[0].someString", "ha"), new Parameter("list[1].someString", "he"));
		assertEquals("ha", list.get(0).getSomeString());
		assertEquals("he", list.get(1).getSomeString());
	}
	
	@Test
	public void canInstantiateAListOfObjectsWithMoreThanOneConstructorParameter() throws Exception {
		final Type type = ContainsParameterizedList.class.getDeclaredField("listOfTwoArguments").getGenericType();
		final Target<List<TwoArguments>> target = new Target<List<TwoArguments>>(type , "theList");
		final List<TwoArguments> theList = iogi.instantiate(target, 
				new Parameter("theList[0].one", "10"), 
				new Parameter("theList[0].two", "11"), 
				new Parameter("theList[1].one", "20"), 
				new Parameter("theList[1].two", "21"));
		assertEquals(10, theList.get(0).getOne());
		assertEquals(11, theList.get(0).getTwo());
		assertEquals(20, theList.get(1).getOne());
		assertEquals(21, theList.get(1).getTwo());
	}
	
	@Test
	public void canInstantiateMoreThanOneListFromTheSameParameterList() throws Exception {
		final Type type = ContainsParameterizedList.class.getDeclaredField("listOfInteger").getGenericType();
		final Target<List<Integer>> firstListTarget = new Target<List<Integer>>(type, "list1");
		final Target<List<Integer>> secondListTarget = new Target<List<Integer>>(type, "list2");
		
		final Parameters parameters = new Parameters(Arrays.asList(
				new Parameter("list1[0]", "10"), 
				new Parameter("list1[1]", "11"), 
				new Parameter("list2[0]", "20"),
				new Parameter("list2[1]", "21")));
		
		final List<Integer> firstList = iogi.instantiate(firstListTarget, parameters);  
		final List<Integer> secondList = iogi.instantiate(secondListTarget, parameters);  
		
		assertThat(firstList, contains(10, 11));
		assertThat(secondList, contains(20, 21));
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=InvalidTypeException.class)
	public void ifTargetIsAListButIsNotParameterizedThrowAnInvalidTypeException() throws Exception {
		 final Type rawListType = List.class;
		 final Target<List> target = new Target<List>(rawListType, "foo");
		 final Parameter parameter = new Parameter("foo[0].bar", "baz");
		 
		 iogi.instantiate(target, parameter);
	}
	
	@Test
	public void canInstantiateACollection() throws Exception {
		final Type parameterizedCollectionType = ContainsAParameterizedCollection.class.getDeclaredField("collectionOfString").getGenericType();
		
		final Target<Collection<String>> target = new Target<Collection<String>>(parameterizedCollectionType, "col");
		final Collection<String> collection = iogi.instantiate(target, new Parameter("col[0]", "bar"), new Parameter("col[1]", "quuux"));
		
		assertThat(collection, contains("bar", "quuux"));
	}
}
