package br.com.caelum.iogi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.iogi.fixtures.OneConstructibleArgument;
import br.com.caelum.iogi.fixtures.OneIntegerPrimitive;
import br.com.caelum.iogi.fixtures.OneStringOneConstructible;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.ParanamerParameterNamesProvider;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.iogi.util.DefaultLocaleProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;

public class ObjectInstantiatorTests {
	private Mockery context;
	private Instantiator<Object> stubInstantiator;

   @SuppressWarnings("unchecked")
   @Before
   public void setUp() {
      context = new Mockery();
      stubInstantiator = context.mock(Instantiator.class);
   }

   private void givenDelegateIsAbleToInstantiateAnything() {
      context.checking(new Expectations() {{
         allowing(stubInstantiator).isAbleToInstantiate(with(any(Target.class)), with(any(Parameters.class)));
         will(returnValue(true));

         allowing(stubInstantiator).instantiate(with(any(Target.class)), with(any(Parameters.class)));
         will(returnValue("x"));
      }});
   }

	@After
	public void tearDown() {
		context.assertIsSatisfied();
	}

	@Test
   public void canInstantiateIfTheRequiredParametersArePresentOnTheRequest() throws Exception {
	   givenDelegateIsAbleToInstantiateAnything();
	   
	   final Target<ConstructorAndProperty> target = Target.create(ConstructorAndProperty.class,  "root");
      final ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator, new NullDependencyProvider(), new ParanamerParameterNamesProvider());
      final Parameters parameters = new Parameters(new Parameter("root.constructorArg", "x"));
      assertTrue(objectInstantiator.isAbleToInstantiate(target, parameters));
      
      final Object object = objectInstantiator.instantiate(target, parameters);
      assertNotNull(object);
      
   }
	
   @Test
   public void canInstantiateIfThereAreExtraParametersInTheRequest() throws Exception {
      givenDelegateIsAbleToInstantiateAnything();
      final Target<ConstructorAndProperty> target = Target.create(ConstructorAndProperty.class, "root");
      final Parameter paramFoundInConstructor = new Parameter("root.constructorArg", "x");
      final Parameter paramFoundSetter = new Parameter("root.propertyValue", "x");

      final ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator,
            new NullDependencyProvider(), new ParanamerParameterNamesProvider());
      final Parameters parameters = new Parameters(paramFoundInConstructor, paramFoundSetter);
      assertTrue(objectInstantiator.isAbleToInstantiate(target, parameters));

      final Object object = objectInstantiator.instantiate(target, parameters);
      assertNotNull(object);
   }
   
	@Test
   public void cannotInstantiateIfThereAreMissingParameters() throws Exception {
      givenDelegateIsAbleToInstantiateAnything();
      
      final Target<ConstructorAndProperty> target = Target.create(ConstructorAndProperty.class, "root");
      final Parameter unreleatedParameter = new Parameter("root.doesNotFulfillAnything", "z");

      final ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator,
            new NullDependencyProvider(), new ParanamerParameterNamesProvider());
      final Parameters parameters = new Parameters(unreleatedParameter);
      assertFalse(objectInstantiator.isAbleToInstantiate(target, parameters));
   }
	
	@Test
   public void cannotInstantiateIfDelegateInstantiatorCannotInstantiateParameter() throws Exception {
	   final Target<ConstructorAndProperty> finalObjectType = Target.create(ConstructorAndProperty.class, "root");
	   final Parameters allParameters = new Parameters(new Parameter("root.constructorArg", "z"));
	   
	   final Target<String> argumentType = Target.create(String.class, "constructorArg");
	   final Parameters argumentParameters = new Parameters(new Parameter("constructorArg", "z"));
	      
      context.checking(new Expectations() {{
         allowing(stubInstantiator).isAbleToInstantiate(argumentType, argumentParameters); will(returnValue(false));
      }});
      
      final ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator,
            new NullDependencyProvider(), new ParanamerParameterNamesProvider());
      
      assertFalse(objectInstantiator.isAbleToInstantiate(finalObjectType, allParameters));
   }

	@Test
	public void willFallbackToSetterIfNoAppropriateConstructorIsFound() throws Exception {
	   givenDelegateIsAbleToInstantiateAnything();
		final Target<ConstructorAndProperty> target = Target.create(ConstructorAndProperty.class,  "root");
		final Parameter paramFoundInConstructor = new Parameter("root.constructorArg", "x");
		final Parameter paramFoundSetter = new Parameter("root.propertyValue", "x");

		final ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator, new NullDependencyProvider(), new ParanamerParameterNamesProvider());
		final ConstructorAndProperty object = (ConstructorAndProperty) objectInstantiator.instantiate(target, new Parameters(paramFoundInConstructor, paramFoundSetter));
		assertEquals("x", object.getConstructorArg());
		assertEquals("x", object.getPropertyValue());
	}

   @Test
   public void ifThereIsMoreThanOneCompatibleConstructorPickTheLargestOne() throws Exception {
      givenDelegateIsAbleToInstantiateAnything();
      final Target<TwoCompatibleConstructors> target = Target.create(TwoCompatibleConstructors.class, "root");
      final Parameter a = new Parameter("root.a", "x");
      final Parameter b = new Parameter("root.b", "x");
      final Parameter c = new Parameter("root.c", "x");
      final Parameter irrelevant = new Parameter("root.irrelevant", "x");

      final ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator,
            new NullDependencyProvider(), new ParanamerParameterNamesProvider());
      final TwoCompatibleConstructors object = (TwoCompatibleConstructors) objectInstantiator.instantiate(target,
            new Parameters(a, b, c, irrelevant));
      assertTrue(object.largestWasCalled);
   }

	@Test
	public void willCallDependencyInjectorForUninstantiableParameters() throws Exception {
	   givenDelegateIsAbleToInstantiateAnything();
		final Target<OneConstructibleArgument> rootTarget = Target.create(OneConstructibleArgument.class, "root");
		final DependencyProvider mockDependencyProvider = context.mock(DependencyProvider.class);
		final Target<OneIntegerPrimitive> argTarget = Target.create(OneIntegerPrimitive.class, "arg");

		final OneIntegerPrimitive injectedValue = new OneIntegerPrimitive(47);
		context.checking(new Expectations() {{
			atLeast(1).of(mockDependencyProvider).canProvide(with(equal(argTarget)));
			will(returnValue(true));

			atLeast(1).of(mockDependencyProvider).provide(argTarget);
			will(returnValue(injectedValue));
		}});

		final ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator, mockDependencyProvider, new ParanamerParameterNamesProvider());
		final OneConstructibleArgument object = (OneConstructibleArgument) objectInstantiator.instantiate(rootTarget, new Parameters(Collections.<Parameter>emptyList()));
		assertSame(injectedValue, object.getArg());
	}

	@Test
	public void willCallDependencyInjectorForUninstantiabelParametersAlongsideInstantiablaParameters() {
	   givenDelegateIsAbleToInstantiateAnything();
		final DependencyProvider mockDependencyProvider = context.mock(DependencyProvider.class);

		final Target<OneStringOneConstructible> rootTarget = Target.create(OneStringOneConstructible.class, "root");
		final Parameters parameters = new Parameters(new Parameter("root.one", "x"));
		final Target<OneIntegerPrimitive> injectableTarget = Target.create(OneIntegerPrimitive.class, "two");

		final OneIntegerPrimitive injectedValue = new OneIntegerPrimitive(47);
		context.checking(new Expectations() {{
			allowing(mockDependencyProvider).canProvide(with(equal(injectableTarget)));
			will(returnValue(true));

			atLeast(1).of(mockDependencyProvider).provide(injectableTarget);
			will(returnValue(injectedValue));
		}});

		final ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator, mockDependencyProvider, new ParanamerParameterNamesProvider());
		final OneStringOneConstructible object = (OneStringOneConstructible) objectInstantiator.instantiate(rootTarget, parameters);

		assertSame(injectedValue, object.getTwo());
		assertEquals("x", object.getOne());
	}

   @Test
   public void shouldInstantiateRecursiveArgumentsGuidedByTheParameters() throws Exception {
      givenDelegateIsAbleToInstantiateAnything();
      Iogi iogi = new Iogi(new NullDependencyProvider(), new DefaultLocaleProvider());
      Recursive newObject = iogi.instantiate(Target.create(Recursive.class, "target"), new Parameters(
            new Parameter("target.r.s", "asdf"), new Parameter("target.s", "asdf")));
      assertNotNull(newObject.r);

   }

   @Test
	public void willUseScalaSetters() throws Exception {
      givenDelegateIsAbleToInstantiateAnything();
		final Target<ScalaObject> target = Target.create(ScalaObject.class,  "root");
		final Parameter paramFoundInConstructor = new Parameter("root.constructorArg", "x");
		final Parameter paramFoundSetter = new Parameter("root.propertyValue", "x");

		final ObjectInstantiator objectInstantiator = new ObjectInstantiator(stubInstantiator, new NullDependencyProvider(), new ParanamerParameterNamesProvider());
		final ScalaObject object = (ScalaObject) objectInstantiator.instantiate(target, new Parameters(paramFoundInConstructor, paramFoundSetter));
		assertEquals("x", object.constructorArg());
		assertEquals("x", object.propertyValue());
	}


    public static class Recursive {
        Recursive r;

        public Recursive(String s) {
        }

        public Recursive(Recursive r, String s) {
            this.r = r;
        }
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

	public static class ScalaObject {
		private final String constructorArg;
		private String propertyValue;

		public ScalaObject(final String constructorArg) {
			this.constructorArg = constructorArg;
		}

		public String constructorArg() {
			return constructorArg;
		}

		public String propertyValue() {
			return propertyValue;
		}

		public void propertyValue_$eq(final String propertyValue) {
			this.propertyValue = propertyValue;
		}
	}
}
