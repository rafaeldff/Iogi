package iogi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class ObjectInstantiatorTests {
	private Mockery context;
	private Instantiator<Object> stubInstantiator;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		context = new Mockery();
		stubInstantiator = context.mock(Instantiator.class);
		context.checking(new Expectations() {{
			allowing(stubInstantiator).isAbleToInstantiate(with(any(Target.class)));
			will(returnValue(true));
			
			allowing(stubInstantiator).instantiate(with(any(Target.class)), with(any(Parameters.class)));
			will(returnValue("x"));
		}});
		
	}

	@Test
	public void canInstantiateIfNoAppropriateConstructorIsFound() throws Exception {
		final Target<ConstructorAndProperty> target = Target.create(ConstructorAndProperty.class,  "root");
		final Parameter paramFoundInConstructor = new Parameter("root.constructorArg", "x");
		final Parameter paramFoundSetter = new Parameter("root.propertyValue", "x");
		
		final ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator, new NullDependencyProvider());
		final Object object = objectInstantiator.instantiate(target, new Parameters(paramFoundInConstructor, paramFoundSetter));
		assertNotNull(object);
	}
	
	@Test
	public void willFallbackToSetterIfNoAppropriateConstructorIsFound() throws Exception {
		final Target<ConstructorAndProperty> target = Target.create(ConstructorAndProperty.class,  "root");
		final Parameter paramFoundInConstructor = new Parameter("root.constructorArg", "x");
		final Parameter paramFoundSetter = new Parameter("root.propertyValue", "x");
		
		final ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator, new NullDependencyProvider());
		final ConstructorAndProperty object = (ConstructorAndProperty) objectInstantiator.instantiate(target, new Parameters(paramFoundInConstructor, paramFoundSetter));
		assertEquals("x", object.getConstructorArg());
		assertEquals("x", object.getPropertyValue());
	}
	
	@Test
	public void ifThereIsMoreThanOneCompatibleConstructorPickTheLargestOne() throws Exception {
		 final Target<TwoCompatibleConstructors> target = Target.create(TwoCompatibleConstructors.class, "root");
		 final Parameter a = new Parameter("root.a", "x");
		 final Parameter b = new Parameter("root.b", "x");
		 final Parameter c = new Parameter("root.c", "x");
		 final Parameter irrelevant = new Parameter("root.irrelevant", "x");
		 
		 final ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator, new NullDependencyProvider());
		 final TwoCompatibleConstructors object = (TwoCompatibleConstructors) objectInstantiator.instantiate(target, new Parameters(a, b, c, irrelevant));
		 assertTrue(object.largestWasCalled);
	}
	
	public static class TwoCompatibleConstructors {
		boolean largestWasCalled = false;
		public TwoCompatibleConstructors(final String a, final String b) {
		}
		public TwoCompatibleConstructors(final String a, final String b, final String c) {
			largestWasCalled = true;
		}
	}
	
	public static class ConstructorAndProperty {
		private final String constructorArg;
		private String propertyValue;
		
		public ConstructorAndProperty(final String constructorArg) {
			this.constructorArg = constructorArg;
		}
		
		public String getConstructorArg() {
			return constructorArg;
		}
		
		public String getPropertyValue() {
			return propertyValue;
		}
		
		public void setPropertyValue(final String propertyValue) {
			this.propertyValue = propertyValue;
		}
	}
}
