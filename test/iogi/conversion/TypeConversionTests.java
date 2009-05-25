package iogi.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public class TypeConversionTests {
	private <T> T convertWith(TypeConverter<T> instantiator, Class<T> type, String stringValue) {
		Target<T> target = Target.create(type, "foo");
		assertTrue(instantiator.isAbleToInstantiate(target));
		return instantiator.instantiate(target, new Parameters(new Parameter("foo", stringValue)));
	}
	
	@Test
	public void doubleConverterCanConverterPrimitiveDoubles() throws Exception {
		double object = convertWith(new DoublePrimitiveConverter(), double.class, "2.0");
		assertEquals(2.0d, object, 0.000001);
	}
	
	@Test
	public void doubleConverterCanConverterWrapperDoubles() throws Exception {
		Double object = convertWith(new DoubleWrapperConverter(), Double.class, "2.0");
		assertEquals(new Double(2.0), object);
	}
	
	@Test
	public void floatConverterCanConvertPrimitiveFloats() throws Exception {
		float object = convertWith(new FloatConverter(), Float.class, "3.14159");
		assertEquals(3.14159f, object, 0.0000001);
	}
	
	@Test
	public void floatConverterCanConvertWrapperFloats() throws Exception {
		Float object = convertWith(new FloatConverter(), Float.class, "3.14159");
		assertEquals(Float.valueOf(3.14159f), object, 0.0000001);
	}
	
	@Test
	public void integerConverterCanConvertPrimitiveIntegers() throws Exception {
		int object = convertWith(new IntegerPrimitiveConverter(), int.class, "2");
		assertEquals(2, object);
	}
	
	@Test
	public void integerConverterCanConvertWrapperIntegers() throws Exception {
		Integer object = convertWith(new IntegerWrapperConverter(), Integer.class, "2");
		assertEquals(Integer.valueOf(2), object);
	}
	
	@Test
	public void shortConverterCanConvertPrimitiveShorts() throws Exception {
		short object = convertWith(new ShortConverter(), Short.class, "2");
		assertEquals(2, object);
	}
	
	@Test
	public void shortConverterCanConvertWrapperShorts() throws Exception {
		Short object = convertWith(new ShortConverter(), Short.class, "2");
		assertEquals(Short.valueOf((short)2), object);
	}
	
	@Test
	public void longConverterCanConverPrimitiveLongs() throws Exception {
		long object = convertWith(new LongConverter(), long.class, "2");
		assertEquals(2l, object);
	}
	
	@Test
	public void longConverterCanConverWrapperLongs() throws Exception {
		Long object = convertWith(new LongConverter(), Long.class, "2");
		assertEquals(Long.valueOf(2l), object);
	}
	
	@Test
	public void stringConverterWillReturnTheParameterValueString() throws Exception {
		String object = convertWith(new StringConverter(), String.class, "foozble");
		assertEquals("foozble", object);
	}
	
	@Test
	public void booleanConverterCanConvertPrimitiveBooleans() throws Exception {
		assertSame(true, convertWith(new BooleanPrimitiveConverter(), boolean.class, "true"));
		assertSame(true, convertWith(new BooleanPrimitiveConverter(), boolean.class, "TRUE"));
		assertSame(false, convertWith(new BooleanPrimitiveConverter(), boolean.class, "asdfs"));
	}
	
	@Test
	public void booleanConverterCanConvertWrapperBooleans() throws Exception {
		assertEquals(Boolean.TRUE, convertWith(new BooleanWrapperConverter(), Boolean.class, "true"));
		assertEquals(Boolean.TRUE, convertWith(new BooleanWrapperConverter(), Boolean.class, "TRUE"));
		assertEquals(Boolean.FALSE, convertWith(new BooleanWrapperConverter(), Boolean.class, "asdfs"));
	}
	
	@Test
	public void bigDecimalConverter() throws Exception {
		assertEquals(BigDecimal.TEN,  convertWith(new BigDecimalConverter(), BigDecimal.class, "10"));
		assertEquals(BigDecimal.valueOf(3.1415926),  convertWith(new BigDecimalConverter(), BigDecimal.class, "3.1415926"));
	}
	
	@Test
	public void bigIntegerConverter() throws Exception {
		BigInteger object = convertWith(new BigIntegerConverter(), BigInteger.class,  "10");
		assertEquals(BigInteger.TEN,  object);
	}
	
	@Test
	public void byteConverterCanConvertPrimitveBytes() throws Exception {
		byte object = convertWith(new BytePrimitiveConverter(), byte.class, "2");
		assertEquals((byte)2, object);
	}
	
	@Test
	public void byteConverterCanConvertWrapperBytes() throws Exception {
		Byte object = convertWith(new ByteWrapperConverter(), Byte.class, "2");
		assertEquals(Byte.valueOf(object), object);
	}
	
	@Test
	public void characterConverterConvertsOneCharStrings() throws Exception {
		char object = convertWith(new CharacterWrapperConverter(), Character.class, "A");
		assertEquals((char)65, object);
	}
	
	@Test
	public void characterConverterConvertsOneCharStringsToAPrimitiveChar() throws Exception {
		char object = convertWith(new CharacterPrimitiveConverter(), char.class, "A");
		assertEquals((char)65, object);
	}
	
	@Test(expected=ConversionException.class)
	public void characterConverterCannotConvertStringsWithMoreThanOneCharacter() throws Exception {
		convertWith(new CharacterWrapperConverter(), Character.class, "AB");
	}
	
	@Test
	public void enumConverterCanConvertLiterals() throws Exception {
		Stooges object = convertWith(new EnumConverter<Stooges>(), Stooges.class, "CURLY");
		assertEquals(Stooges.CURLY, object);
	}
	
	@Test
	public void enumConverterCanConvertOrdinals() {
		Stooges object = convertWith(new EnumConverter<Stooges>(), Stooges.class, "1");
		assertEquals(Stooges.CURLY, object);
	}
	
	@Test(expected=ConversionException.class)
	public void enumConverterWillThrowAnExceptionIfGivenAnUnrecognizedString() throws Exception {
		convertWith(new EnumConverter<Stooges>(), Stooges.class, "LAUREL");
	}
	
	@Test(expected=ConversionException.class)
	public void enumConverterWillThrowAnExceptionIfGivenAnInvalidOrdinal() throws Exception {
		String overNineThousand = "9001";
		convertWith(new EnumConverter<Stooges>(), Stooges.class, overNineThousand);
	}
	
	static enum Stooges {
		MOE,
		CURLY,
		LARRY
	}
}
