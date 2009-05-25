package iogi;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import org.junit.Test;

public class ArrayInstantiatorTests {
	@Test
	public void canInstantiateAnArrayOfStrings() throws Exception {
		Parameters parameters = new Parameters(new Parameter("arr[0]", "one"), new Parameter("arr[1]", "two"));
		Instantiator<String[]> arrayInstantiator = new ArrayInstantiator<String>();
		Target<String[]> target = Target.create(String[].class, "arr");
		assertTrue(arrayInstantiator.isAbleToInstantiate(target));
		String[] array = arrayInstantiator.instantiate(target, parameters);
		assertArrayEquals(new String[] {"one", "two"}, array);
	}
}
