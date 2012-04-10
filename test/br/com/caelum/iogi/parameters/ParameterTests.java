package br.com.caelum.iogi.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
	
	@Test
	public void ifTheParameterNameDoesntHaveDotsThenStripReturnsEmptyString() {
		final Parameter parameter = new Parameter("wholename", "");
		assertEquals("", parameter.strip().getFirstNameComponent());
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
	
	@Test
	public void isDecoratedIfFirstnameComponentHasABracket() throws Exception {
		assertTrue(new Parameter("asdf[0].k","").isDecorated()); 
		assertFalse(new Parameter("asdf.k","").isDecorated()); 
	}
}
