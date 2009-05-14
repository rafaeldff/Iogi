package iogi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class IogiTests {
	private Instantiatior instantiatior = new Instantiatior();
	
	@Test public void testSimplyInstantiate() {
		Empty empty = instantiatior.instantiate(Empty.class, "empty");
		assertEquals(empty.getClass(), Empty.class);
	}
	
	@Test
	public void testInstantiateWithOneIntegerArgument() throws Exception {
		OneIntegerPrimitive object = instantiatior.instantiate(OneIntegerPrimitive.class, "oneArg.anInteger", "42");
		assertEquals(42, object.getAnInteger());
	}
	
	@Test
	public void testInstantiateWithOneDoubleArgument() throws Exception {
		OneDoublePrimitive object = instantiatior.instantiate(OneDoublePrimitive.class, "oneArg.aDouble", "42.0");
		assertEquals(42.0, object.getADouble(), 0.001);
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
}
