package iogi.collections;

import static org.junit.Assert.assertEquals;
import iogi.Iogi;
import iogi.exceptions.InvalidTypeException;
import iogi.fixtures.MixedObjectAndList;
import iogi.fixtures.OneString;
import iogi.fixtures.TwoArguments;
import iogi.parameters.Parameter;
import iogi.reflection.Target;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;


public class ListInstantiatonTests {
	private Iogi iogi = new Iogi();
	
	public static class ContainsParameterizedList {
		List<OneString> listOfOneString;
		List<TwoArguments> listOfTwoArguments;
		List<Integer> listOfInteger;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void canInstantiateAListOfObjects() throws Exception {
		Parameter firstParameter = new Parameter("root.someString", "bla");
		Parameter secondParameter = new Parameter("root.someString", "ble");
		
		Type parameterizedListType = ContainsParameterizedList.class.getDeclaredField("listOfOneString").getGenericType();
		
		Target<List> target = new Target(parameterizedListType, "root");
		List objects = iogi.instantiate(target, firstParameter, secondParameter);
		
		assertEquals(2, objects.size());
		OneString first = (OneString)objects.get(0);
		assertEquals(first.getSomeString(), "bla");
		OneString second = (OneString)objects.get(1);
		assertEquals(second.getSomeString(), "ble");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void canInstantiateAListOfPrimitives() throws Exception {
		Parameter firstParameter = new Parameter("root", "1");
		Parameter secondParameter = new Parameter("root", "0");
		
		Type parameterizedListType = ContainsParameterizedList.class.getDeclaredField("listOfInteger").getGenericType();
		
		Target<List> target = new Target(parameterizedListType, "root");
		List objects = iogi.instantiate(target, firstParameter, secondParameter);
		
		assertEquals(2, objects.size());
		int first = (Integer) objects.get(0);
		assertEquals(1, first);
		int second = (Integer)objects.get(1);
		assertEquals(0, second);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=InvalidTypeException.class)
	public void ifTargetIsAListButIsNotParameterizedThrowAnInvalidTypeException() throws Exception {
		 Type rawListType = List.class;
		 Target<List> target = new Target<List>(rawListType, "foo");
		 Parameter parameter = new Parameter("foo.bar", "baz");
		 
		 iogi.instantiate(target, parameter);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void canInstantiateAListWhoseElementsHaveMoreThanOneConstructorParameter() throws Exception {
		Parameter p1 = new Parameter("root.one", "1");
		Parameter p2 = new Parameter("root.two", "2");
		Parameter p3 = new Parameter("root.one", "11");
		Parameter p4 = new Parameter("root.two", "22");
		
		Type parameterizedListType = ContainsParameterizedList.class.getDeclaredField("listOfTwoArguments").getGenericType();
		
		Target<List> target = new Target(parameterizedListType, "root");
		List objects = iogi.instantiate(target, p1, p2, p3, p4);
		
		assertEquals(2, objects.size());
		TwoArguments first = (TwoArguments)objects.get(0);
		assertEquals(1, first.getOne());
		assertEquals(2, first.getTwo());
		TwoArguments second = (TwoArguments)objects.get(1);
		assertEquals(11, second.getOne());
		assertEquals(22, second.getTwo());
	}
	
	@Test
	public void canInstantiateMixingListsAndRegularObjects() throws Exception {
		Parameter firstParameter = new Parameter("root.list.someString", "bla");
		Parameter secondParameter = new Parameter("root.list.someString", "ble");
		Parameter thirdParameter = new Parameter("root.object.someString", "blu");
		
		
		Target<MixedObjectAndList> target = Target.create(MixedObjectAndList.class, "root");
		MixedObjectAndList root = iogi.instantiate(target, firstParameter, secondParameter, thirdParameter);
		
		assertEquals(2, root.getList().size());
		OneString first = (OneString)root.getList().get(0);
		assertEquals("bla", first.getSomeString());
		OneString second = (OneString)root.getList().get(1);
		assertEquals("ble", second.getSomeString());
		
		assertEquals("blu", root.getObject().getSomeString());
	}
}
