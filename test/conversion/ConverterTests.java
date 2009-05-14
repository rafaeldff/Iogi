package conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConverterTests {
	@Test
	public void doubleConverterForPrimitive() throws Exception {
		Converter<Double> converter = new DoubleConverter(); 
		assertTrue(converter.isAbleToConvertTo(double.class));
		assertEquals(Double.valueOf(2.0), converter.convert("2.0", double.class));
	}
	
	@Test
	public void doubleConverterForObject() throws Exception {
		Converter<Double> converter = new DoubleConverter(); 
		assertTrue(converter.isAbleToConvertTo(Double.class));
		assertEquals(new Double(2.0), converter.convert("2.0", Double.class));
	}
}
