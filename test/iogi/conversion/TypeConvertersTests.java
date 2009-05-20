package iogi.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import iogi.Instantiator;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.math.BigDecimal;
import java.math.BigInteger;
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
	
	@Test
	public void booleanConverter() throws Exception {
		Instantiator<Boolean> converter = new BooleanConverter();
		Target<Boolean> target = Target.create(Boolean.class, "param");
		assertTrue(converter.isAbleToInstantiate(target));
		assertTrue(converter.instantiate(target, oneParameter("param", "true")));
		assertTrue(converter.instantiate(target, oneParameter("param", "TRUE")));
		assertFalse(converter.instantiate(target, oneParameter("param", "asdf")));
	}
	
	@Test
	public void bigDecimalConverter() throws Exception {
		Instantiator<BigDecimal> converter = new BigDecimalConveter(); 
		Target<BigDecimal> target = Target.create(BigDecimal.class, "param");
		assertTrue(converter.isAbleToInstantiate(target));
		assertEquals(BigDecimal.TEN,  converter.instantiate(target, oneParameter("param", "10")));
		assertEquals(BigDecimal.valueOf(3.1415926),  converter.instantiate(target, oneParameter("param", "3.1415926")));
	}
	
	@Test
	public void bigIntegerConverter() throws Exception {
		Instantiator<BigInteger> converter = new BigIntegerConverter(); 
		Target<BigInteger> target = Target.create(BigInteger.class, "param");
		assertTrue(converter.isAbleToInstantiate(target));
		assertEquals(BigInteger.TEN,  converter.instantiate(target, oneParameter("param", "10")));
	}
	
	@Test
	public void byteConverter() throws Exception {
		Instantiator<Byte> converter = new ByteConverter(); 
		Target<Byte> target = Target.create(Byte.class, "foo");
		assertTrue(converter.isAbleToInstantiate(target));
		assertEquals((byte)2, (byte)converter.instantiate(target, oneParameter("foo", "2")));
	}
	
	@Test
	public void characterConverterConvertsOneCharStrings() throws Exception {
		Instantiator<Character> converter = new CharacterConverter(); 
		Target<Character> target = Target.create(Character.class, "foo");
		assertTrue(converter.isAbleToInstantiate(target));
		assertEquals((char)65, (char)converter.instantiate(target, oneParameter("foo", "A")));
	}
	
	@Test(expected=ConversionException.class)
	public void characterConverterCannotConvertStringsWithMoreThanOneCharacter() throws Exception {
		Instantiator<Character> converter = new CharacterConverter(); 
		Target<Character> target = Target.create(Character.class, "foo");
		assertTrue(converter.isAbleToInstantiate(target));
		converter.instantiate(target, oneParameter("foo", "AB"));
	}
	
	@Test
	public void enumConverter() throws Exception {
		Instantiator<Stooges> converter = new EnumConverter<Stooges>(); 
		Target<Stooges> target = Target.create(Stooges.class, "foo");
		assertTrue(converter.isAbleToInstantiate(target));
		assertEquals(Stooges.CURLY, converter.instantiate(target, oneParameter("foo", "CURLY")));
	}
	
	//TODO: more enum tests...
	
	static enum Stooges {
		MOE,
		CURLY,
		LARRY
	}
}
