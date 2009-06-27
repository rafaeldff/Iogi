package iogi.collections;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import iogi.Iogi;
import iogi.exceptions.InvalidTypeException;
import iogi.fixtures.ContainsAParameterizedCollection;
import iogi.fixtures.ContainsParameterizedList;
import iogi.fixtures.MixedObjectAndList;
import iogi.fixtures.OneString;
import iogi.fixtures.TwoArguments;
import iogi.parameters.Parameter;
import iogi.reflection.Target;
import iogi.util.DefaultLocaleProvider;
import iogi.util.NullDependencyProvider;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.junit.Test;


public class CyclingListInstantiatonTests {
	private final Iogi iogi = new Iogi(new NullDependencyProvider(), new DefaultLocaleProvider());
	
	@SuppressWarnings("unchecked")
	@Test
	public void canInstantiateAListOfObjects() throws Exception {
		final Parameter firstParameter = new Parameter("root.someString", "bla");
		final Parameter secondParameter = new Parameter("root.someString", "ble");
		
		final Type parameterizedListType = ContainsParameterizedList.class.getDeclaredField("listOfOneString").getGenericType();
		
		final Target<List> target = new Target(parameterizedListType, "root");
		final List objects = iogi.instantiate(target, firstParameter, secondParameter);
		
		assertEquals(2, objects.size());
		final OneString first = (OneString)objects.get(0);
		assertEquals(first.getSomeString(), "bla");
		final OneString second = (OneString)objects.get(1);
		assertEquals(second.getSomeString(), "ble");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void canInstantiateAListOfPrimitives() throws Exception {
		final Parameter firstParameter = new Parameter("root", "1");
		final Parameter secondParameter = new Parameter("root", "0");
		
		final Type parameterizedListType = ContainsParameterizedList.class.getDeclaredField("listOfInteger").getGenericType();
		
		final Target<List> target = new Target(parameterizedListType, "root");
		final List objects = iogi.instantiate(target, firstParameter, secondParameter);
		
		assertEquals(2, objects.size());
		final int first = (Integer) objects.get(0);
		assertEquals(1, first);
		final int second = (Integer)objects.get(1);
		assertEquals(0, second);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=InvalidTypeException.class)
	public void ifTargetIsAListButIsNotParameterizedThrowAnInvalidTypeException() throws Exception {
		 final Type rawListType = List.class;
		 final Target<List> target = new Target<List>(rawListType, "foo");
		 final Parameter parameter = new Parameter("foo.bar", "baz");
		 
		 iogi.instantiate(target, parameter);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void canInstantiateAListWhoseElementsHaveMoreThanOneConstructorParameter() throws Exception {
		final Parameter p1 = new Parameter("root.one", "1");
		final Parameter p2 = new Parameter("root.two", "2");
		final Parameter p3 = new Parameter("root.one", "11");
		final Parameter p4 = new Parameter("root.two", "22");
		
		final Type parameterizedListType = ContainsParameterizedList.class.getDeclaredField("listOfTwoArguments").getGenericType();
		
		final Target<List> target = new Target(parameterizedListType, "root");
		final List objects = iogi.instantiate(target, p1, p2, p3, p4);
		
		assertEquals(2, objects.size());
		final TwoArguments first = (TwoArguments)objects.get(0);
		assertEquals(1, first.getOne());
		assertEquals(2, first.getTwo());
		final TwoArguments second = (TwoArguments)objects.get(1);
		assertEquals(11, second.getOne());
		assertEquals(22, second.getTwo());
	}
	
	@Test
	public void canInstantiateMixingListsAndRegularObjects() throws Exception {
		final Parameter firstParameter = new Parameter("root.list.someString", "bla");
		final Parameter secondParameter = new Parameter("root.list.someString", "ble");
		final Parameter thirdParameter = new Parameter("root.object.someString", "blu");
		
		
		final Target<MixedObjectAndList> target = Target.create(MixedObjectAndList.class, "root");
		final MixedObjectAndList root = iogi.instantiate(target, firstParameter, secondParameter, thirdParameter);
		
		assertEquals(2, root.getList().size());
		final OneString first = (OneString)root.getList().get(0);
		assertEquals("bla", first.getSomeString());
		final OneString second = (OneString)root.getList().get(1);
		assertEquals("ble", second.getSomeString());
		
		assertEquals("blu", root.getObject().getSomeString());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void willInstantiateAnEmptyListIfGivenNoAppropriatedParameters() throws Exception {
		final Type parameterizedListType = ContainsParameterizedList.class.getDeclaredField("listOfOneString").getGenericType();
		
		final Target<List> target = new Target(parameterizedListType, "root");
		final List objects = iogi.instantiate(target);
		
		assertTrue(objects.isEmpty());
	}
	
	@Test
	public void canInstantiateACollection() throws Exception {
		final Type parameterizedCollectionType = ContainsAParameterizedCollection.class.getDeclaredField("collectionOfString").getGenericType();
		
		final Target<Collection<String>> target = new Target<Collection<String>>(parameterizedCollectionType, "col");
		final Collection<String> collection = iogi.instantiate(target, new Parameter("col", "bar"), new Parameter("col", "quuux"));
		
		assertThat(collection, contains("bar", "quuux"));
	}
}
