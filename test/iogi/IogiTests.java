package iogi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import iogi.exceptions.InvalidTypeException;
import iogi.exceptions.NoConstructorFoundException;
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
	public void canInstantiateAList() throws Exception {
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
		
		
		Target<MixedPrimitiveAndList> target = Target.create(MixedPrimitiveAndList.class, "root");
		MixedPrimitiveAndList root = iogi.instantiate(target, firstParameter, secondParameter, thirdParameter);
		
		assertEquals(2, root.getList().size());
		OneString first = (OneString)root.getList().get(0);
		assertEquals("bla", first.getSomeString());
		OneString second = (OneString)root.getList().get(1);
		assertEquals("ble", second.getSomeString());
		
		assertEquals("blu", root.getObject().getSomeString());
	}
	
	abstract static class AbstractClass {
	}
	
	static class OneString {
		private final String someString;

		public OneString(String someString) {
			this.someString = someString;
		}
		
		public String getSomeString() {
			return someString;
		}
	}
	
	static class OneIntegerPrimitive {
		private int anInteger;

		public OneIntegerPrimitive(int anInteger) {
			this.anInteger = anInteger;
		}
		
		public int getAnInteger() {
			return anInteger;
		}
	}
	
	static class OneDoublePrimitive {
		private double aDouble;
		
		public OneDoublePrimitive(double aDouble) {
			this.aDouble = aDouble;
		}
		
		public double getADouble() {
			return aDouble;
		}
	}
	
	static class TwoArguments {
		private int one;
		private int two;

		public TwoArguments(int one, int two) {
			this.one = one;
			this.two = two;
		}

		public int getOne() {
			return one;
		}

		public int getTwo() {
			return two;
		}
	}
	
	static class OneConstructibleArgument {
		private final OneIntegerPrimitive arg;

		public OneConstructibleArgument(OneIntegerPrimitive arg) {
			this.arg = arg;
		}
		
		public OneIntegerPrimitive getArg() {
			return arg;
		}
	}
	
	static class TwoConstructors {
		public TwoConstructors(int one, int two) {
		}
		
		public TwoConstructors(String a, String b) {
		}
	}
	
	static class TwoConstructibleArguments {
		private final OneString one;
		private final OneIntegerPrimitive two;

		public TwoConstructibleArguments(OneString one, OneIntegerPrimitive two) {
			this.one = one;
			this.two = two;
		}

		public OneString getOne() {
			return one;
		}

		public OneIntegerPrimitive getTwo() {
			return two;
		}
	}
	
	static class TwoLevelConstructible {
		private final OneConstructibleArgument level2;

		public TwoLevelConstructible(OneConstructibleArgument level2) {
			this.level2 = level2;
		}
		
		public OneConstructibleArgument getLevel2() {
			return level2;
		}
	}
	
	static class MixedPrimitiveAndConstructibleArguments {
		private final int one;
		private final OneIntegerPrimitive two;

		public MixedPrimitiveAndConstructibleArguments(int one,  OneIntegerPrimitive two) {
			this.one = one;
			this.two = two;
		}

		public int getOne() {
			return one;
		}

		public OneIntegerPrimitive getTwo() {
			return two;
		}
	}
	
	static class MixedPrimitiveAndList {
		private final List<OneString> list;
		private final OneString object;

		public MixedPrimitiveAndList(List<OneString> list, OneString object) {
			this.list = list;
			this.object = object;
		}
		
		public List<OneString> getList() {
			return list;
		}
		
		public OneString getObject() {
			return object;
		}
	}
	
	static class ContainsParameterizedList {
		List<OneString> listOfOneString;
		List<TwoArguments> listOfTwoArguments;
	}
}
