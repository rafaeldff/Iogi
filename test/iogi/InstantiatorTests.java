package iogi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;

public class InstantiatorTests {	
	private Instantiatior instantiator = new Instantiatior();
	
	@Test
	public void canInstantiatePrimitives() {
		Target<Integer> target = new Target<Integer>(int.class, "any");
		Integer primitive = instantiator.instantiate(target, new Parameter("any", "25"));
		assertEquals(25, primitive.intValue());
	}
	
	@Test
	public void canInstantiatePrimitivesWithWrapperTarget() {
		Target<Integer> target = new Target<Integer>(Integer.class, "any");
		Integer primitive = instantiator.instantiate(target, new Parameter("any", "25"));
		assertEquals(25, primitive.intValue());
	}
	
	@Test
	public void canInstantiateWithOneIntegerArgument() throws Exception {
		Target<OneIntegerPrimitive> target = new Target<OneIntegerPrimitive>(OneIntegerPrimitive.class, "oneArg");
		OneIntegerPrimitive object = instantiator.instantiate(target, new Parameter("oneArg.anInteger", "42"));
		assertEquals(42, object.getAnInteger());
	}
	
	@Test
	public void canInstantiateWithOneDoubleArgument() throws Exception {
		Target<OneDoublePrimitive> target = new Target<OneDoublePrimitive>(OneDoublePrimitive.class, "oneArg");
		OneDoublePrimitive object = instantiator.instantiate(target, new Parameter("oneArg.aDouble", "42.0"));
		assertEquals(42.0, object.getADouble(), 0.001);
	}
	
	@Test
	public void canInstantiateWithTwoPrimitiveArguments() throws Exception {
		Parameter first = new Parameter("twoArguments.one", "1");
		Parameter second = new Parameter("twoArguments.two", "2");
		Target<TwoArguments> target = new Target<TwoArguments>(TwoArguments.class, "twoArguments");
		TwoArguments object = instantiator.instantiate(target, Arrays.asList(first, second));
		assertEquals(1, object.getOne());
		assertEquals(2, object.getTwo());
	}
	
	@Test
	public void canUseTheAppropriatedConstructorWhenThereAreMany() {
		Parameter first = new Parameter("twoConstructors.one", "1");
		Parameter second = new Parameter("twoConstructors.two", "2");
		Target<TwoConstructors> target = new Target<TwoConstructors>(TwoConstructors.class, "twoConstructors");
		TwoConstructors object = instantiator.instantiate(target, first, second);
		assertNotNull(object);
	}
	
	@Test
	public void canInstantiateRecursevly() throws Exception {
		Parameter param = new Parameter("oneConstructibleArgument.arg.anInteger", "8");
		Target<OneConstructibleArgument> target = new Target<OneConstructibleArgument>(OneConstructibleArgument.class, "oneConstructibleArgument");
		OneConstructibleArgument object = instantiator.instantiate(target, param);
		assertEquals(8, object.getArg().getAnInteger());
	}
	
	@Test
	public void ignoresParametersThatArentRelatedToTheTarget() throws Exception {
		Parameter relevantParam = new Parameter("relevant.someString", "ok");
		Parameter irrelevantParam = new Parameter("irrelevant.someString", "not ok");
		Target<OneString> target = new Target<OneString>(OneString.class, "relevant");
		OneString object = instantiator.instantiate(target, relevantParam, irrelevantParam);
		assertEquals("ok", object.getSomeString());
	}
	
	@Test
	public void ignoresParametersThatArentRelatedToTheTargetRegardlessOfOrder() throws Exception {
		Parameter relevantParam = new Parameter("relevant.someString", "ok");
		Parameter irrelevantParam = new Parameter("irrelevant.someString", "not ok");
		Target<OneString> target = new Target<OneString>(OneString.class, "relevant");
		
		OneString instantiatedWithOneOrder = instantiator.instantiate(target, relevantParam, irrelevantParam);
		assertEquals("ok", instantiatedWithOneOrder.getSomeString());
		
		OneString instantiatedWithAnotherOrder = instantiator.instantiate(target, relevantParam, irrelevantParam);
		assertEquals("ok", instantiatedWithAnotherOrder.getSomeString());
	}
	
	@Test
	public void canRecursivelyInstantiateMultipleParameters() throws Exception {
		Parameter parameter1 = new Parameter("root.one.someString", "a");
		Parameter parameter2 = new Parameter("root.two.anInteger", "2");
		Target<TwoConstructibleArguments> target = new Target<TwoConstructibleArguments>(TwoConstructibleArguments.class, "root");
		
		TwoConstructibleArguments object = instantiator.instantiate(target, parameter1, parameter2);
		assertEquals("a", object.getOne().getSomeString());
		assertEquals(2, object.getTwo().getAnInteger());
	}
	
	@Test
	public void canInstantiateTwoWithTwoLevelsOfRecursiveInstantiation() throws Exception {
		Parameter parameter = new Parameter("root.level2.arg.anInteger", "42"); 
		Target<TwoLevelConstructible> target = new Target<TwoLevelConstructible>(TwoLevelConstructible.class, "root");
		TwoLevelConstructible object = instantiator.instantiate(target, parameter);
		assertEquals(42, object.getLevel2().getArg().getAnInteger());
	}
	
	@Test
	public void canMixConstructibleAndPrimitiveArgumeynts() throws Exception {
		Parameter primitiveParameter = new Parameter("root.one", "555");
		Parameter constructibleParameter = new Parameter("root.two.anInteger", "666");
		Target<MixedPrimitiveAndConstructibleArguments> target = new Target<MixedPrimitiveAndConstructibleArguments>(MixedPrimitiveAndConstructibleArguments.class, "root");
		MixedPrimitiveAndConstructibleArguments object = instantiator.instantiate(target, primitiveParameter, constructibleParameter);
		assertEquals(555, object.getOne());
		assertEquals(666, object.getTwo().getAnInteger());
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
}
