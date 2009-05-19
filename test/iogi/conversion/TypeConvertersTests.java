package iogi.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import iogi.Parameter;
import iogi.Parameters;
import iogi.Target;

import java.util.Arrays;

import org.junit.Test;

public class TypeConvertersTests {
	private Parameters oneParameter(String key, String value) {
		return new Parameters(new Parameter(key, value));
	}
	
	@Test
	public void doubleConverterForPrimitive() throws Exception {
		Instantiator<Double> converter = new DoubleConverter(); 
		Target<Double> target = Target.create(double.class, "foo");
		assertTrue(converter.isAbleToInstantiate(target));
		assertEquals(Double.valueOf(2.0), converter.instantiate(target, oneParameter("foo", "2.0")));
	}
	
	@Test
	public void doubleConverterForObject() throws Exception {
		Instantiator<Double> converter = new DoubleConverter(); 
		Target<Double> target = Target.create(Double.class, "foo");
		assertTrue(converter.isAbleToInstantiate(target));
		assertEquals(new Double(2.0), converter.instantiate(target, oneParameter("foo", "2.0")));
	}
	
	@Test
	public void integerConverter() throws Exception {
		Instantiator<Integer> converter = new IntegerConverter(); 
		Target<Integer> target = Target.create(Integer.class, "foo");
		assertTrue(converter.isAbleToInstantiate(target));
		assertEquals(Integer.valueOf(2), converter.instantiate(target, oneParameter("foo", "2")));
	}
	
	@Test
	public void stringConverter() throws Exception {
		Instantiator<String> converter = new StringConverter();
		Target<String> target = Target.create(String.class, "param");
		assertTrue(converter.isAbleToInstantiate(target));
		assertEquals("foozble", converter.instantiate(target, new Parameters(Arrays.asList(new Parameter("param", "foozble")))));
	}
}
