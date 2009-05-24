package iogi.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import iogi.Instantiator;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FalllbackInstantiatorTests {
	private Mockery context;
	private Instantiator<Object> delegateInstantiator;
	private Object anything = new Object();
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		context = new Mockery();
		delegateInstantiator = context.mock(Instantiator.class);
	}
	
	@After
	public void tearDown() {
		context.assertIsSatisfied();
	}

	@Test
	public void fallbackInstantiatorIsAbleToInstantiateATargetIfTheDelegateInstantiatorIsAbleToDoIt() throws Exception {
		final Target<Object> target = Target.create(Object.class, "foo");
		
		context.checking(new Expectations() {{
			allowing(delegateInstantiator).isAbleToInstantiate(target);
			will(returnValue(true));
		}});
		
		Instantiator<Object> fallbackInstantiator = new FallbackInstantiator<Object>(delegateInstantiator, anything);
		assertTrue(fallbackInstantiator.isAbleToInstantiate(target));
	}
	
	@Test
	public void fallbackInstantiatorIsNotAbleToInstantiateATargetIfTheDelegateInstantiatorIsntAbleToDoIt() throws Exception {
		final Target<Object> target = Target.create(Object.class, "foo");
		
		context.checking(new Expectations() {{
			allowing(delegateInstantiator).isAbleToInstantiate(target);
			will(returnValue(false));
		}});
		
		Instantiator<Object> fallbackInstantiator = new FallbackInstantiator<Object>(delegateInstantiator, anything);
		assertFalse(fallbackInstantiator.isAbleToInstantiate(target));
	}
	
	@Test
	public void fallbackInstantiatorWillCallDelegateInstantiatorIfParameterValueIsNotEmpty() throws Exception {
		final Target<Object> target = Target.create(Object.class, "foo");
		final Parameters parameters = new Parameters(new Parameter("foo", "bar"));
		
		context.checking(new Expectations() {{
			one(delegateInstantiator).instantiate(with(equal(target)), with(equal(parameters)));
		}});
		
		Instantiator<Object> fallbackInstantiator = new FallbackInstantiator<Object>(delegateInstantiator, anything);
		fallbackInstantiator.instantiate(target, parameters);
	}
	
	@Test
	public void fallbackInstantiatorWillReturnTheFallbackValueIfParameterValueIsEmpty() throws Exception {
		final Target<Object> target = Target.create(Object.class, "foo");
		final Parameters parameters = new Parameters(new Parameter("foo", ""));
		
		Object fallbackValue = new Object();
		Instantiator<Object> fallbackInstantiator = new FallbackInstantiator<Object>(delegateInstantiator, fallbackValue);
		assertEquals(fallbackValue, fallbackInstantiator.instantiate(target, parameters));
	}
}
