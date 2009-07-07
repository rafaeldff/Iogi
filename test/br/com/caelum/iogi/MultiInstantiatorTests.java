package br.com.caelum.iogi;

import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.iogi.exceptions.InvalidTypeException;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;


public class MultiInstantiatorTests {
	private final Mockery context = new Mockery();
	private Instantiator<?> mockSubInstantiator;
	private List<Instantiator<?>> theMockInstantiator;
	
	@Before
	public void setUp() {
		this.mockSubInstantiator = context.mock(Instantiator.class);
		theMockInstantiator = Collections.<Instantiator<?>>singletonList(mockSubInstantiator);
	}
	
	@After
	public void tearDown() {
		context.assertIsSatisfied();
	}
	
	@Test
	public void multiInstantiatorWillUseAnAbleInstantiator() throws Exception {
		final MultiInstantiator multiInstantiator = new MultiInstantiator(theMockInstantiator);
		
		context.checking(new Expectations() {{
			allowing(mockSubInstantiator).isAbleToInstantiate(with(any(Target.class)));
			will(returnValue(true));
			
			one(mockSubInstantiator).instantiate(with(any(Target.class)), (with(any(Parameters.class))));
		}});
		
		multiInstantiator.instantiate(Target.create(Object.class, ""), null);
	}
	
	@Test(expected=InvalidTypeException.class)
	public void ifNoAbleInstantiatorIsFoundWillThrowAnException() {
		final MultiInstantiator mutliInstantiator = new MultiInstantiator(theMockInstantiator);
		
		context.checking(new Expectations() {{
			allowing(mockSubInstantiator).isAbleToInstantiate(with(any(Target.class)));
			will(returnValue(false));
		}});
		
		mutliInstantiator.instantiate(Target.create(Object.class, ""), null);
	}
}
