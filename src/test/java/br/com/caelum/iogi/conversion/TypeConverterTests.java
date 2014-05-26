package br.com.caelum.iogi.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.hamcrest.Matchers;
import org.junit.Test;

import br.com.caelum.iogi.exceptions.ConversionException;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

public class TypeConverterTests {
	@Test
	public void callingInstantiateWillTriggerACallToTheConvertAbstractMethod() throws Exception {
		final Object stubConverted = new Object();
		class MockTypeConverter extends TypeConverter<Object> {
			@Override
			protected Object convert(final String stringValue, final Target<?> to) {
				return stubConverted;
			}
			
			public boolean isAbleToInstantiate(final Target<?> target) {
				return false;
			}
		}

		final MockTypeConverter typeConverter = new MockTypeConverter();
		final Object instantiated = typeConverter.instantiate(Target.create(String.class, ""), new Parameters(new Parameter("", "")));
		assertEquals(stubConverted, instantiated);
	}
	
	@Test
	public void ifAnArbitratyExceptionIsThrownWhileConvertingItWillBeWrappedInAConvertionException() throws Exception {
		final ArithmeticException expectedWrappedException = new ArithmeticException();
		class MockTypeConverter extends TypeConverter<Fizzble> {
			@Override
			protected Fizzble convert(final String stringValue, final Target<?> to) {
				throw expectedWrappedException;
			}
			
			public boolean isAbleToInstantiate(final Target<?> target) {
				return false;
			}
		}
		final MockTypeConverter typeConverter = new MockTypeConverter();
		try {
			typeConverter.instantiate(Target.create(Fizzble.class, "foo"), new Parameters(new Parameter("foo", "oops")));
			fail();
		}
		catch(final ConversionException thrownException) {
			assertEquals(expectedWrappedException, thrownException.getCause());
			final String string = thrownException.getMessage();
			assertThat(string,  Matchers.<String>both(containsString("Fizzble")).and(containsString("foo"))/*.and(containsString("oops"))*/);
		}
	}
	
	@Test
	public void ifAConvertionExceptionIsThrownWhileConvertingItWillBeRethrown() throws Exception {
		final ConversionException expectedWrappedException = new ConversionException("");
		class MockTypeConverter extends TypeConverter<Fizzble> {
			@Override
			protected Fizzble convert(final String stringValue, final Target<?> to) {
				throw expectedWrappedException;
			}
			
			public boolean isAbleToInstantiate(final Target<?> target) {
				return false;
			}
		}
		final MockTypeConverter typeConverter = new MockTypeConverter();
		try {
			typeConverter.instantiate(Target.create(Fizzble.class, "foo"), new Parameters(new Parameter("foo", "oops")));
			fail();
		}
		catch(final Exception thrownException) {
			assertEquals(expectedWrappedException, thrownException);
		}
	}
	
	
	
	static class Fizzble {}
}
