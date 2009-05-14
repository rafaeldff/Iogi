package iogi;

import iogi.conversion.ConversionException;
import iogi.conversion.TypeConverter;

import java.util.Collections;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ConverterTest {
	private Mockery context = new Mockery();
	private TypeConverter<?> typeConverter;
	private Set<TypeConverter<?>> theTypeConverter;
	
	@Before
	public void setUp() {
		this.typeConverter = context.mock(TypeConverter.class);
		theTypeConverter = Collections.<TypeConverter<?>>singleton(typeConverter);
	}
	
	@After
	public void tearDown() {
		context.assertIsSatisfied();
	}
	
	@Test
	public void converterWillUseAnAbleTypeConverter() throws Exception {
		Converter converter = new Converter(theTypeConverter);
		
		context.checking(new Expectations() {{
			allowing(typeConverter).isAbleToConvertTo(with(any(Class.class)));
			will(returnValue(true));
			
			one(typeConverter).convert(with(any(String.class)), with(any(Class.class)));
		}});
		
		converter.convert("", Void.class);
	}
	
	@Test(expected=ConversionException.class)
	public void ifNoAbleConverterIsFoundWillThrowAnException() {
		Converter converter = new Converter(theTypeConverter);
		
		context.checking(new Expectations() {{
			allowing(typeConverter).isAbleToConvertTo(with(any(Class.class)));
			will(returnValue(false));
		}});
		
		converter.convert("", Void.class);
	}
}
