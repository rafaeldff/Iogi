package iogi;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;


public class IogiTests {
	private Instantiatior instantiatior = new Instantiatior();
	
	@Test
	public void canInstantiateWithOneIntegerArgument() throws Exception {
		OneIntegerPrimitive object = instantiatior.instantiate(OneIntegerPrimitive.class, new Parameter("oneArg.anInteger", "42"));
		assertEquals(42, object.getAnInteger());
	}
	
	@Test
	public void canInstantiateWithOneDoubleArgument() throws Exception {
		OneDoublePrimitive object = instantiatior.instantiate(OneDoublePrimitive.class, new Parameter("oneArg.aDouble", "42.0"));
		assertEquals(42.0, object.getADouble(), 0.001);
	}
	
	@Test
	public void canInstantiateWithTwoPrimitiveArguments() throws Exception {
		Parameter first = new Parameter("twoArguments.one", "1");
		Parameter second = new Parameter("twoArguments.two", "2");
		TwoArguments object = instantiatior.instantiate(TwoArguments.class, Arrays.asList(first, second));
		assertEquals(1, object.getOne());
		assertEquals(2, object.getTwo());
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
}
