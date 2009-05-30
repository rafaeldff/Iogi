package iogi;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import iogi.collections.ListInstantiatorTests.ContainsParameterizedList;
import iogi.exceptions.InvalidTypeException;
import iogi.exceptions.NoConstructorFoundException;
import iogi.fixtures.AbstractClass;
import iogi.fixtures.MixedObjectAndList;
import iogi.fixtures.MixedPrimitiveAndConstructibleArguments;
import iogi.fixtures.OneArgOneProperty;
import iogi.fixtures.OneConstructibleArgument;
import iogi.fixtures.OneDoublePrimitive;
import iogi.fixtures.OneIntegerPrimitive;
import iogi.fixtures.OneString;
import iogi.fixtures.TwoArguments;
import iogi.fixtures.TwoConstructibleArguments;
import iogi.fixtures.TwoConstructors;
import iogi.fixtures.TwoLevelConstructible;
import iogi.fixtures.TwoProperties;
import iogi.parameters.Parameter;
import iogi.reflection.Target;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;

public class IogiTests {
	private Iogi iogi = new Iogi();
	
	@Test
	public void canInstantiatePrimitives() {
		Target<Integer> target = Target.create(int.class, "any");
		Integer primitive = iogi.instantiate(target, new Parameter("any", "25"));
		assertEquals(25, primitive.intValue());
	}
	
	@Test
	public void canInstantiatePrimitivesWithWrapperTarget() {
		Target<Integer> target = Target.create(Integer.class, "any");
		Integer primitive = iogi.instantiate(target, new Parameter("any", "25"));
		assertEquals(25, primitive.intValue());
	}
	
	@Test
	public void canInstantiateWithOneIntegerArgument() throws Exception {
		Target<OneIntegerPrimitive> target = Target.create(OneIntegerPrimitive.class, "oneArg");
		OneIntegerPrimitive object = iogi.instantiate(target, new Parameter("oneArg.anInteger", "42"));
		assertEquals(42, object.getAnInteger());
	}
	
	@Test
	public void canInstantiateWithOneDoubleArgument() throws Exception {
		Target<OneDoublePrimitive> target = Target.create(OneDoublePrimitive.class, "oneArg");
		OneDoublePrimitive object = iogi.instantiate(target, new Parameter("oneArg.aDouble", "42.0"));
		assertEquals(42.0, object.getADouble(), 0.001);
	}
	
	@Test
	public void canInstantiateWithTwoPrimitiveArguments() throws Exception {
		Parameter first = new Parameter("twoArguments.one", "1");
		Parameter second = new Parameter("twoArguments.two", "2");
		Target<TwoArguments> target = Target.create(TwoArguments.class, "twoArguments");
		TwoArguments object = iogi.instantiate(target, first, second);
		assertEquals(1, object.getOne());
		assertEquals(2, object.getTwo());
	}
	
	@Test
	public void canUseTheAppropriatedConstructorWhenThereAreMany() {
		Parameter first = new Parameter("twoConstructors.one", "1");
		Parameter second = new Parameter("twoConstructors.two", "2");
		Target<TwoConstructors> target = Target.create(TwoConstructors.class, "twoConstructors");
		TwoConstructors object = iogi.instantiate(target, first, second);
		assertNotNull(object);
	}
	
	@Test
	public void canInstantiateRecursevly() throws Exception {
		Parameter param = new Parameter("oneConstructibleArgument.arg.anInteger", "8");
		Target<OneConstructibleArgument> target = Target.create(OneConstructibleArgument.class, "oneConstructibleArgument");
		OneConstructibleArgument object = iogi.instantiate(target, param);
		assertEquals(8, object.getArg().getAnInteger());
	}
	
	@Test
	public void ignoresParametersThatArentRelatedToTheTarget() throws Exception {
		Parameter relevantParam = new Parameter("relevant.someString", "ok");
		Parameter irrelevantParam = new Parameter("irrelevant.someString", "not ok");
		Target<OneString> target = Target.create(OneString.class, "relevant");
		OneString object = iogi.instantiate(target, relevantParam, irrelevantParam);
		assertEquals("ok", object.getSomeString());
	}
	
	@Test
	public void ignoresParametersThatArentRelatedToTheTargetRegardlessOfOrder() throws Exception {
		Parameter relevantParam = new Parameter("relevant.someString", "ok");
		Parameter irrelevantParam = new Parameter("irrelevant.someString", "not ok");
		Target<OneString> target = Target.create(OneString.class, "relevant");
		
		OneString instantiatedWithOneOrder = iogi.instantiate(target, relevantParam, irrelevantParam);
		assertEquals("ok", instantiatedWithOneOrder.getSomeString());
		
		OneString instantiatedWithAnotherOrder = iogi.instantiate(target, relevantParam, irrelevantParam);
		assertEquals("ok", instantiatedWithAnotherOrder.getSomeString());
	}
	
	@Test
	public void canRecursivelyInstantiateMultipleParameters() throws Exception {
		Parameter parameter1 = new Parameter("root.one.someString", "a");
		Parameter parameter2 = new Parameter("root.two.anInteger", "2");
		Target<TwoConstructibleArguments> target = Target.create(TwoConstructibleArguments.class, "root");
		
		TwoConstructibleArguments object = iogi.instantiate(target, parameter1, parameter2);
		assertEquals("a", object.getOne().getSomeString());
		assertEquals(2, object.getTwo().getAnInteger());
	}
	
	@Test
	public void canInstantiateTwoWithTwoLevelsOfRecursiveInstantiation() throws Exception {
		Parameter parameter = new Parameter("root.level2.arg.anInteger", "42"); 
		Target<TwoLevelConstructible> target = Target.create(TwoLevelConstructible.class, "root");
		TwoLevelConstructible object = iogi.instantiate(target, parameter);
		assertEquals(42, object.getLevel2().getArg().getAnInteger());
	}
	
	@Test
	public void canMixConstructibleAndPrimitiveArguments() throws Exception {
		Parameter primitiveParameter = new Parameter("root.one", "555");
		Parameter constructibleParameter = new Parameter("root.two.anInteger", "666");
		Target<MixedPrimitiveAndConstructibleArguments> target = Target.create(MixedPrimitiveAndConstructibleArguments.class, "root");
		MixedPrimitiveAndConstructibleArguments object = iogi.instantiate(target, primitiveParameter, constructibleParameter);
		assertEquals(555, object.getOne());
		assertEquals(666, object.getTwo().getAnInteger());
	}
	
	@Test
	public void willCallSettersForPropertiesThatCouldNotBeFilledByAConstructor() throws Exception {
		 Parameter oneProperty = new Parameter("root.oneProperty", "5");
		 Parameter oneArg = new Parameter("root.oneArg", "3.14");
		 Target<OneArgOneProperty> target = Target.create(OneArgOneProperty.class, "root");
		 OneArgOneProperty object = iogi.instantiate(target, oneProperty, oneArg);
		 assertEquals((double)object.getOneArg(), 3.14, 0.01);
		 assertEquals(object.getOneProperty(), 5);
	}
	
	@Test
	public void ifThereIsNoConstructorWithArgumentsWillCallTheDefaultConstructorAndFillPropertiesThroughSetters()  throws Exception {
		Parameter one = new Parameter("root.one", "9001");
		Parameter two = new Parameter("root.two", "9002");
		Target<TwoProperties> target = Target.create(TwoProperties.class, "root");
		TwoProperties object = iogi.instantiate(target, one, two);
		assertEquals(object.getOne(), 9001);
		assertEquals(object.getTwo(), 9002);
	}
	
	@Test(expected=NoConstructorFoundException.class)
	public void testWillThrowANoConstructorFoundExceptionIfNoAdequateConstructorIsFound() {
		Parameter aParameter = new Parameter("root.a", "");
		Target<OneIntegerPrimitive> target = Target.create(OneIntegerPrimitive.class, "root");
		iogi.instantiate(target, aParameter);
	}
	
	@Test(expected=InvalidTypeException.class)
	public void willThrowAnInvalidTypeExceptionIfGivenAnInterface() throws Exception {
		Parameter aParameter = new Parameter("root.a", "");
		Target<CharSequence> target = Target.create(CharSequence.class, "root");
		iogi.instantiate(target, aParameter);
	}
	
	@Test(expected=InvalidTypeException.class)
	public void willThrowAnInvalidTypeExceptionIfGivenAnAbstractClass() throws Exception {
		Parameter aParameter = new Parameter("root.a", "");
		Target<AbstractClass> target = Target.create(AbstractClass.class, "root");
		iogi.instantiate(target, aParameter);
	}
	
	@Test(expected=InvalidTypeException.class)
	public void willThrowAnInvalidTypeExceptionIfGivenVoid() throws Exception {
		Parameter aParameter = new Parameter("root.a", "");
		Target<Void> target = Target.create(Void.class, "root");
		iogi.instantiate(target, aParameter);
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
	
	@Test
	public void emptyIntegerParametersWillBeInstantiatedAsZero() throws Exception {
		 OneIntegerPrimitive object = iogi.instantiate(Target.create(OneIntegerPrimitive.class, "foo"), new Parameter("foo.anInteger", ""));
		 assertEquals(0, object.getAnInteger());
	}
	
	@Test
	public void emptyDoubleParametersWillBeInstantiatedAsZero() throws Exception {
		OneDoublePrimitive object = iogi.instantiate(Target.create(OneDoublePrimitive.class, "foo"), new Parameter("foo.aDouble", ""));
		assertEquals(0d, object.getADouble(), 0.00d);
	}
	
	@Test
	public void canInstantiateAnArrayOfStrings() throws Exception {
		Target<String[]> target = Target.create(String[].class, "arr");
		String[] array = iogi.instantiate(target, new Parameter("arr[0]", "one"), new Parameter("arr[1]", "two"));
		assertArrayEquals(new String[] {"one", "two"}, array);
	}
	
	@Test
	public void canInstantiateAnArrayOfIntegerWrappers() throws Exception {
		Target<Integer[]> target = Target.create(Integer[].class, "arr");
		Integer[] array = iogi.instantiate(target, new Parameter("arr[0]", "99"), new Parameter("arr[1]", "98"));
		assertArrayEquals(new Integer[] {99, 98}, array);
	}
	
	@Test
	public void canInstantiateAnArrayOfPrimitiveIntegers() throws Exception {
		Target<int[]> target = Target.create(int[].class, "arr");
		int[] array = iogi.instantiate(target, new Parameter("arr[0]", "99"), new Parameter("arr[1]", "98"));
		assertArrayEquals(new int[] {99, 98}, array);
	}
	
	@Test
	public void canInstantiateAnArrayOfObjects() throws Exception {
		Target<OneIntegerPrimitive[]> target = Target.create(OneIntegerPrimitive[].class, "arr");
		OneIntegerPrimitive[] array = iogi.instantiate(target, new Parameter("arr[0].anInteger", "99"), new Parameter("arr[1].anInteger", "98"));
		assertEquals(99, array[0].getAnInteger());
		assertEquals(98, array[1].getAnInteger());
	}
	
	@Test
	public void canInstantiateAnArrayOfObjectsWithMoreThanOneConstructorParameter() throws Exception {
		Target<TwoArguments[]> target = Target.create(TwoArguments[].class, "arr");
		TwoArguments[] array = iogi.instantiate(target, 
				new Parameter("arr[0].one", "10"), 
				new Parameter("arr[0].two", "11"), 
				new Parameter("arr[1].one", "20"), 
				new Parameter("arr[1].two", "21"));
		assertEquals(10, array[0].getOne());
		assertEquals(11, array[0].getTwo());
		assertEquals(20, array[1].getOne());
		assertEquals(21, array[1].getTwo());
	}
	
	@Test
	public void canMixArraysWithNonArraysAsParametersForAConstructor() throws Exception {
		Target<MixedObjectAndArray> target = Target.create(MixedObjectAndArray.class, "root");
		MixedObjectAndArray rootObject = iogi.instantiate(target, 
				new Parameter("root.array[0].someString", "10"), 
				new Parameter("root.array[1].someString", "20"),
				new Parameter("root.object.someString", "00")); 
		
		assertEquals("00", rootObject.getObject().getSomeString());
		assertEquals("10", rootObject.getArray()[0].getSomeString());
		assertEquals("20", rootObject.getArray()[1].getSomeString());
	}
}
