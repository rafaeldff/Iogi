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
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FalllbackConverterTests {
	private Mockery context;
	private TypeConverter<Object> delegateConverter;
	private final Object anything = new Object();
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		context = new Mockery();
		context.setImposteriser(ClassImposteriser.INSTANCE);
		delegateConverter = context.mock(TypeConverter.class);
	}
	
	@After
	public void tearDown() {
		context.assertIsSatisfied();
	}

	@Test
	public void fallbackInstantiatorIsAbleToInstantiateATargetIfTheDelegateInstantiatorIsAbleToDoIt() throws Exception {
		final Target<Object> target = Target.create(Object.class, "foo");
		
		context.checking(new Expectations() {{
			allowing(delegateConverter).isAbleToInstantiate(target);
			will(returnValue(true));
		}});
		
		final Instantiator<Object> fallbackInstantiator = new FallbackConverter<Object>(delegateConverter, anything);
		assertTrue(fallbackInstantiator.isAbleToInstantiate(target));
	}
	
	@Test
	public void fallbackInstantiatorIsNotAbleToInstantiateATargetIfTheDelegateInstantiatorIsntAbleToDoIt() throws Exception {
		final Target<Object> target = Target.create(Object.class, "foo");
		
		context.checking(new Expectations() {{
			allowing(delegateConverter).isAbleToInstantiate(target);
			will(returnValue(false));
		}});
		
		final Instantiator<Object> fallbackInstantiator = new FallbackConverter<Object>(delegateConverter, anything);
		assertFalse(fallbackInstantiator.isAbleToInstantiate(target));
	}
	
	@Test
	public void fallbackInstantiatorWillCallDelegateConverterIfParameterValueIsNotEmpty() throws Exception {
		final Target<Object> target = Target.create(Object.class, "foo");
		final Parameters parameters = new Parameters(new Parameter("foo", "bar"));
		
		context.checking(new Expectations() {{
			one(delegateConverter).convert("bar", target);
		}});
		
		final Instantiator<Object> fallbackInstantiator = new FallbackConverter<Object>(delegateConverter, anything);
		fallbackInstantiator.instantiate(target, parameters);
	}
	
	@Test
	public void fallbackInstantiatorWillReturnTheFallbackValueIfParameterValueIsEmpty() throws Exception {
		final Target<Object> target = Target.create(Object.class, "foo");
		final Parameters parameters = new Parameters(new Parameter("foo", ""));
		
		final Object fallbackValue = new Object();
		final Instantiator<Object> fallbackInstantiator = new FallbackConverter<Object>(delegateConverter, fallbackValue);
		assertEquals(fallbackValue, fallbackInstantiator.instantiate(target, parameters));
	}
}
