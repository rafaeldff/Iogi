package br.com.caelum.iogi.conversion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.caelum.iogi.Iogi;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.util.DefaultLocaleProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;

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
