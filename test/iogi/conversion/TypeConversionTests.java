package iogi.conversion;

import static org.junit.Assert.assertEquals;
import iogi.Iogi;
import iogi.NullDependencyProvider;
import iogi.parameters.Parameter;
import iogi.reflection.Target;

import org.junit.Test;

public class TypeConversionTests {
	private Iogi iogi = new Iogi(new NullDependencyProvider());
	
	@Test
	public void canInstantiatePrimitives() {
		Target<Integer> target = Target.create(int.class, "any");
		Integer primitive = iogi.instantiate(target, new Parameter("any", "25"));
		assertEquals(25, primitive.intValue());
	}
	
	@Test
	public void canInstantiatePrimitivesWithWrapperTarget() {
		Target<Integer> target = Target.create(Integer.class, "any");
		Integer primitive = iogi.instantiate(target, new Parameter("any", "25"));
		assertEquals(25, primitive.intValue());
	}
}
