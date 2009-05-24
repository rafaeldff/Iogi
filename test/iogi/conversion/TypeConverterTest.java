package iogi.conversion;

import static org.junit.Assert.assertEquals;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import org.junit.Test;


public class TypeConverterTest {
	@Test
	public void callingInstantiateWillTriggerACallToTheConvertAbstractMethod() throws Exception {
		final Object stubConverted = new Object();
		class MockTypeConverter extends TypeConverter<Object> {
			@Override
			public Object convert(String stringValue) {
				return stubConverted;
			}

			@Override
			public boolean isAbleToInstantiate(Target<?> target) {
				return false;
			}
		}

		MockTypeConverter mockTypeConverter = new MockTypeConverter();
		Object instantiated = mockTypeConverter.instantiate(Target.create(String.class, ""), new Parameters(new Parameter("", "")));
		assertEquals(stubConverted, instantiated);
	}
}
