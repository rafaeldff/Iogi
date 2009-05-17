package iogi.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import iogi.Target;

import org.junit.Test;

public class TypeConvertersTests {
	@Test
	public void doubleConverterForPrimitive() throws Exception {
		TypeConverter<Double> converter = new DoubleConverter(); 
		assertTrue(converter.isAbleToConvertTo(double.class));
		assertEquals(Double.valueOf(2.0), converter.convert("2.0", target(double.class), null));
	}
	
	@Test
	public void doubleConverterForObject() throws Exception {
		TypeConverter<Double> converter = new DoubleConverter(); 
		assertTrue(converter.isAbleToConvertTo(Double.class));
		assertEquals(new Double(2.0), converter.convert("2.0", target(Double.class), null));
	}
	
	@Test
	public void integerConverter() throws Exception {
		TypeConverter<Integer> converter = new IntegerConverter(); 
		assertTrue(converter.isAbleToConvertTo(Integer.class));
		assertEquals(Integer.valueOf(2), converter.convert("2", target(Integer.class), null));
	}
	
	@Test
	public void stringConverter() throws Exception {
		TypeConverter<String> converter = new StringConverter();
		assertTrue(converter.isAbleToConvertTo(String.class));
		assertEquals("foozble", converter.convert("foozble", target(Integer.class), null));
	}
	
	private <T> Target<T> target(Class<T> type) {
		return Target.create(type, "");
	}
}
