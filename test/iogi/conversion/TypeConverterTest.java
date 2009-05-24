package iogi.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import org.junit.Test;


public class TypeConverterTest {
	@Test
	public void isAbleToConvertWillReturnTrueIfTheTargetHasTheSameClassMatchingAsTheConverterTargetClass() throws Exception {
		 TypeConverter<String> converter = new TypeConverter<String>() {
			 @Override
			 public Class<String> targetClass() {
				 return String.class;
			 }

			@Override
			public String convert(String stringValue) {
				return null;
			}
		 }; 
		 assertTrue(converter.isAbleToInstantiate(Target.create(String.class, "")));
	}
	
	@Test
	public void isAbleToConvertWillNotReturnTrueIfTheTargetHasADifferentClassThanTheConverterTargetClass() throws Exception {
		TypeConverter<Integer> converter = new TypeConverter<Integer>() {
			@Override
			public Class<Integer> targetClass() {
				return Integer.class;
			}

			@Override
			public Integer convert(String stringValue) {
				return null;
			}
		}; 
		assertFalse(converter.isAbleToInstantiate(Target.create(String.class, "")));
	}
	
	@Test
	public void callingInstantiateWillTriggerACallToTheConvertAbstractMethod() throws Exception {
		final Object stubConverted = new Object();
		class MockTypeConverter extends TypeConverter<Object> {
			@Override
			public Class<Object> targetClass() {
				return null;
			}


			@Override
			public Object convert(String stringValue) {
				return stubConverted;
			}
		}

		MockTypeConverter mockTypeConverter = new MockTypeConverter();
		Object instantiated = mockTypeConverter.instantiate(Target.create(String.class, ""), new Parameters(new Parameter("", "")));
		assertEquals(stubConverted, instantiated);
	}
	
	
}
