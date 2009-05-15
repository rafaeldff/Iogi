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
	
	static class Empty {
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
}
