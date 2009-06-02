package iogi.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class ParameterTests {
	@Test
	public void ifTheParameterNameDoesntHaveDotsThenGetFirstNameComponentReturnsTheWholeName() throws Exception {
		 final Parameter parameter = new Parameter("wholename", "");
		 assertEquals("wholename", parameter.getFirstNameComponent());
	}
	
	@Test
	public void ifTheParameterNameHasTwoComponentsGetFirstNameComponentReturnsTheFirst() {
		final Parameter parameter = new Parameter("first.last", "");
		assertEquals("first", parameter.getFirstNameComponent());
	}
	
	@Test
	public void ifTheParameterNameHasThreeComponentsGetFirstNameComponentReturnsTheFirst() {
		final Parameter parameter = new Parameter("one.two.three", "");
		assertEquals("one", parameter.getFirstNameComponent());
	}
	
	public void ifTheParameterNameDoesntHaveDotsThenStripReturnsNull() {
		final Parameter parameter = new Parameter("wholename", "");
		assertNull(parameter.strip());
	}
	
	@Test
	public void ifTheParameterNameHasTwoComponentsSplitReturnsAParameterContainingTheRestOfTheArguments() {
		final Parameter parameter = new Parameter("head.tail.tail", "");
		assertEquals("tail.tail", parameter.strip().getName());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cannotCreateAParameterWithNullName() throws Exception {
		new Parameter(null, "not null"); 
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cannotCreateAParameterWithNullValue() throws Exception {
		new Parameter("not null", null); 
	}
}
