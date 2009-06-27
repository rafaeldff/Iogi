package iogi.conversion;

import static org.junit.Assert.assertEquals;
import iogi.Iogi;
import iogi.parameters.Parameter;
import iogi.reflection.Target;
import iogi.util.DefaultLocaleProvider;
import iogi.util.NullDependencyProvider;

import org.junit.Test;

public class TypeConversionTests {
	private final Iogi iogi = new Iogi(new NullDependencyProvider(), new DefaultLocaleProvider());
	
	@Test
	public void canInstantiatePrimitives() {
		final Target<Integer> target = Target.create(int.class, "any");
		final Integer primitive = iogi.instantiate(target, new Parameter("any", "25"));
		assertEquals(25, primitive.intValue());
	}
	
	@Test
	public void canInstantiatePrimitivesWithWrapperTarget() {
		final Target<Integer> target = Target.create(Integer.class, "any");
		final Integer primitive = iogi.instantiate(target, new Parameter("any", "25"));
		assertEquals(25, primitive.intValue());
	}
}
