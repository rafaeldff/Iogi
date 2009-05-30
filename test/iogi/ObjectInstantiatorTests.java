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
		Target<ConstructorAndProperty> target = Target.create(ConstructorAndProperty.class,  "root");
		Parameter paramFoundInConstructor = new Parameter("root.constructorArg", "x");
		Parameter paramFoundSetter = new Parameter("root.propertyValue", "x");
		
		ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator, new NullDependencyProvider());
		Object object = objectInstantiator.instantiate(target, new Parameters(paramFoundInConstructor, paramFoundSetter));
		assertNotNull(object);
	}
	
	@Test
	public void willFallbackToSetterIfNoAppropriateConstructorIsFound() throws Exception {
		Target<ConstructorAndProperty> target = Target.create(ConstructorAndProperty.class,  "root");
		Parameter paramFoundInConstructor = new Parameter("root.constructorArg", "x");
		Parameter paramFoundSetter = new Parameter("root.propertyValue", "x");
		
		ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator, new NullDependencyProvider());
		ConstructorAndProperty object = (ConstructorAndProperty) objectInstantiator.instantiate(target, new Parameters(paramFoundInConstructor, paramFoundSetter));
		assertEquals("x", object.getConstructorArg());
		assertEquals("x", object.getPropertyValue());
	}
	
	@Test
	public void ifThereIsMoreThanOneCompatibleConstructorPickTheLargestOne() throws Exception {
		 Target<TwoCompatibleConstructors> target = Target.create(TwoCompatibleConstructors.class, "root");
		 Parameter a = new Parameter("root.a", "x");
		 Parameter b = new Parameter("root.b", "x");
		 Parameter c = new Parameter("root.c", "x");
		 Parameter irrelevant = new Parameter("root.irrelevant", "x");
		 
		 ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator, new NullDependencyProvider());
		 TwoCompatibleConstructors object = (TwoCompatibleConstructors) objectInstantiator.instantiate(target, new Parameters(a, b, c, irrelevant));
		 assertTrue(object.largestWasCalled);
	}
	
	public static class TwoCompatibleConstructors {
		boolean largestWasCalled = false;
		public TwoCompatibleConstructors(String a, String b) {
		}
		public TwoCompatibleConstructors(String a, String b, String c) {
			largestWasCalled = true;
		}
	}
	
	public static class ConstructorAndProperty {
		private String constructorArg;
		private String propertyValue;
		
		public ConstructorAndProperty(String constructorArg) {
			this.constructorArg = constructorArg;
		}
		
		public String getConstructorArg() {
			return constructorArg;
		}
		
		public String getPropertyValue() {
			return propertyValue;
		}
		
		public void setPropertyValue(String propertyValue) {
			this.propertyValue = propertyValue;
		}
	}
}
