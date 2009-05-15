package iogi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class ParameterTest {
	@Test
	public void ifTheParameterNameDoesntHaveDotsThenGetFirstNameComponentReturnsTheWholeName() throws Exception {
		 Parameter parameter = new Parameter("wholename", "");
		 assertEquals("wholename", parameter.getFirstNameComponent());
	}
	
	@Test
	public void ifTheParameterNameHasTwoComponentsGetFirstNameComponentReturnsTheFirst() {
		Parameter parameter = new Parameter("first.last", "");
		assertEquals("first", parameter.getFirstNameComponent());
	}
	
	@Test
	public void ifTheParameterNameHasThreeComponentsGetFirstNameComponentReturnsTheFirst() {
		Parameter parameter = new Parameter("one.two.three", "");
		assertEquals("one", parameter.getFirstNameComponent());
	}
	
	public void ifTheParameterNameDoesntHaveDotsThenStripReturnsNull() {
		Parameter parameter = new Parameter("wholename", "");
		assertNull(parameter.strip());
	}
	
	@Test
	public void ifTheParameterNameHasTwoComponentsSplitReturnsAParameterContaining() {
		Parameter parameter = new Parameter("first.last", "");
		assertEquals("last", parameter.strip().getName());
	}
}
