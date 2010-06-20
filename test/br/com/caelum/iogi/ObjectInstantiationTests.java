package br.com.caelum.iogi;

import br.com.caelum.iogi.collections.ArrayInstantiator;
import br.com.caelum.iogi.collections.ListInstantiator;
import br.com.caelum.iogi.conversion.TypeConverter;
import br.com.caelum.iogi.exceptions.InvalidTypeException;
import br.com.caelum.iogi.fixtures.*;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.ParanamerParameterNamesProvider;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.iogi.util.DefaultLocaleProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static br.com.caelum.iogi.conversion.FallbackConverter.fallbackToNull;
import static org.junit.Assert.*;

public class ObjectInstantiationTests {
	private final Iogi iogi = new Iogi(new NullDependencyProvider(), new DefaultLocaleProvider());
	
	@Test
	public void willReturnNullIfNoAppropriateParameterIsFound() throws Exception {
		final Target<OneString> target = Target.create(OneString.class, "root");
		final OneString object = iogi.instantiate(target);
		assertNull(object);
	}
	
	@Test
	public void willReturnNullIfNoAppropriateParameterIsFoundWithString() throws Exception {
		final Target<String> target = Target.create(String.class, "root");
		final String object = iogi.instantiate(target);
		assertNull(object);
	}
	
	@Test
	public void canInstantiateWithOneIntegerArgument() throws Exception {
		final Target<OneIntegerPrimitive> target = Target.create(OneIntegerPrimitive.class, "oneArg");
		final OneIntegerPrimitive object = iogi.instantiate(target, new Parameter("oneArg.anInteger", "42"));
		assertEquals(42, object.getAnInteger());
	}
	
	@Test
	public void canInstantiateWithOneDoubleArgument() throws Exception {
		final Target<OneDoublePrimitive> target = Target.create(OneDoublePrimitive.class, "oneArg");
		final OneDoublePrimitive object = iogi.instantiate(target, new Parameter("oneArg.aDouble", "42.0"));
		assertEquals(42.0, object.getADouble(), 0.001);
	}
	
	@Test
	public void canInstantiateWithTwoPrimitiveArguments() throws Exception {
		final Parameter first = new Parameter("twoArguments.one", "1");
		final Parameter second = new Parameter("twoArguments.two", "2");
		final Target<TwoArguments> target = Target.create(TwoArguments.class, "twoArguments");
		final TwoArguments object = iogi.instantiate(target, first, second);
		assertEquals(1, object.getOne());
		assertEquals(2, object.getTwo());
	}
	
	@Test
	public void canUseTheAppropriatedConstructorWhenThereAreMany() {
		final Parameter first = new Parameter("twoConstructors.one", "1");
		final Parameter second = new Parameter("twoConstructors.two", "2");
		final Target<TwoConstructors> target = Target.create(TwoConstructors.class, "twoConstructors");
		final TwoConstructors object = iogi.instantiate(target, first, second);
		assertNotNull(object);
	}
	
	@Test
	public void canInstantiateRecursevly() throws Exception {
		final Parameter param = new Parameter("oneConstructibleArgument.arg.anInteger", "8");
		final Target<OneConstructibleArgument> target = Target.create(OneConstructibleArgument.class, "oneConstructibleArgument");
		final OneConstructibleArgument object = iogi.instantiate(target, param);
		assertEquals(8, object.getArg().getAnInteger());
	}
	
	@Test
	public void ignoresParametersThatArentRelatedToTheTarget() throws Exception {
		final Parameter relevantParam = new Parameter("relevant.someString", "ok");
		final Parameter irrelevantParam = new Parameter("irrelevant.someString", "not ok");
		final Target<OneString> target = Target.create(OneString.class, "relevant");
		final OneString object = iogi.instantiate(target, relevantParam, irrelevantParam);
		assertEquals("ok", object.getSomeString());
	}
	
	@Test
	public void ignoresParametersThatArentRelatedToTheTargetRegardlessOfOrder() throws Exception {
		final Parameter relevantParam = new Parameter("relevant.someString", "ok");
		final Parameter irrelevantParam = new Parameter("irrelevant.someString", "not ok");
		final Target<OneString> target = Target.create(OneString.class, "relevant");
		
		final OneString instantiatedWithOneOrder = iogi.instantiate(target, relevantParam, irrelevantParam);
		assertEquals("ok", instantiatedWithOneOrder.getSomeString());
		
		final OneString instantiatedWithAnotherOrder = iogi.instantiate(target, relevantParam, irrelevantParam);
		assertEquals("ok", instantiatedWithAnotherOrder.getSomeString());
	}
	
	@Test
	public void canRecursivelyInstantiateMultipleParameters() throws Exception {
		final Parameter parameter1 = new Parameter("root.one.someString", "a");
		final Parameter parameter2 = new Parameter("root.two.anInteger", "2");
		final Target<TwoConstructibleArguments> target = Target.create(TwoConstructibleArguments.class, "root");
		
		final TwoConstructibleArguments object = iogi.instantiate(target, parameter1, parameter2);
		assertEquals("a", object.getOne().getSomeString());
		assertEquals(2, object.getTwo().getAnInteger());
	}
	
	@Test
	public void canInstantiateTwoWithTwoLevelsOfRecursiveInstantiation() throws Exception {
		final Parameter parameter = new Parameter("root.level2.arg.anInteger", "42"); 
		final Target<TwoLevelConstructible> target = Target.create(TwoLevelConstructible.class, "root");
		final TwoLevelConstructible object = iogi.instantiate(target, parameter);
		assertEquals(42, object.getLevel2().getArg().getAnInteger());
	}
	
	@Test
	public void canMixConstructibleAndPrimitiveArguments() throws Exception {
		final Parameter primitiveParameter = new Parameter("root.one", "555");
		final Parameter constructibleParameter = new Parameter("root.two.anInteger", "666");
		final Target<MixedPrimitiveAndConstructibleArguments> target = Target.create(MixedPrimitiveAndConstructibleArguments.class, "root");
		final MixedPrimitiveAndConstructibleArguments object = iogi.instantiate(target, primitiveParameter, constructibleParameter);
		assertEquals(555, object.getOne());
		assertEquals(666, object.getTwo().getAnInteger());
	}
	
	@Test
	public void willCallSettersForPropertiesThatCouldNotBeFilledByAConstructor() throws Exception {
		 final Parameter oneProperty = new Parameter("root.oneProperty", "5");
		 final Parameter oneArg = new Parameter("root.oneArg", "3.14");
		 final Target<OneArgOneProperty> target = Target.create(OneArgOneProperty.class, "root");
		 final OneArgOneProperty object = iogi.instantiate(target, oneProperty, oneArg);
		 assertEquals(object.getOneArg(), 3.14, 0.01);
		 assertEquals(object.getOneProperty(), 5);
	}

    @Test
    public void willNotChangeClassLoaderURLsNorTheClassLoaderItself() throws Exception {
        final URL fooJarUrl = ObjectInstantiationTests.class.getResource("/foo.jar");
        final URL[] originalClassLoaderURLs = {fooJarUrl};
        final URLClassLoader classLoader = new URLClassLoader(originalClassLoaderURLs, this.getClass().getClassLoader());
        
        final Parameter oneProperty = new Parameter("bean.class.classLoader.URLs[0]", "http://atack.com/my.jar");
        final Target<Object> target = new Target<Object>(classLoader.loadClass("Foo"), "bean");
        final Object object = new IogiWithUrlConverter().instantiate(target, new Parameters(oneProperty));

        assertArrayEquals(originalClassLoaderURLs, ((URLClassLoader)object.getClass().getClassLoader()).getURLs());
        assertSame(classLoader, object.getClass().getClassLoader());
    }


    @Test
	public void willIgnorePropertiesForWhichThereAreNoParameters() throws Exception {
		final Parameter one = new Parameter("root.one", "1");
		final Target<TwoProperties> target = Target.create(TwoProperties.class, "root");
		final TwoProperties object = iogi.instantiate(target, one);
		assertEquals(1, object.getOne());
	}
	
	@Test
	public void ifThereIsNoConstructorWithArgumentsWillCallTheDefaultConstructorAndFillPropertiesThroughSetters()  throws Exception {
		final Parameter one = new Parameter("root.one", "9001");
		final Parameter two = new Parameter("root.two", "9002");
		final Target<TwoProperties> target = Target.create(TwoProperties.class, "root");
		final TwoProperties object = iogi.instantiate(target, one, two);
		assertEquals(9001, object.getOne());
		assertEquals(9002, object.getTwo());
	}
	
	@Test
	public void willCallSettersEvenIfTheyDeclareAReturnType() throws Exception {
		final Parameter oneProperty = new Parameter("root.oneProperty", "666");
		final Target<OnePropertyWithReturnType> target = Target.create(OnePropertyWithReturnType.class, "root");
		final OnePropertyWithReturnType object = iogi.instantiate(target, oneProperty);
		assertEquals(666, object.getOneProperty());
	}
	
	@Test
	public void canInstantiateAndSetAList() throws Exception {
		final Parameter one = new Parameter("root.list.anInteger", "-1");
		final Parameter two = new Parameter("root.list.anInteger", "-2");

		final Target<OneGenericListProperty> target = Target.create(OneGenericListProperty.class, "root");
		final List<OneIntegerPrimitive> list = iogi.instantiate(target, one, two).getList();
		
		assertEquals(2, list.size());
		assertEquals(-1, list.get(0).getAnInteger());
		assertEquals(-2, list.get(1).getAnInteger());
	}
	
	@Test
	public void testWillReturnNullIfNoAdequateConstructorIsFound() {
		final Parameter aParameter = new Parameter("root.a", "");
		final Target<OneIntegerPrimitive> target = Target.create(OneIntegerPrimitive.class, "root");
		final OneIntegerPrimitive object = iogi.instantiate(target, aParameter);
		assertNull(object);
	}
	
	@Test(expected=InvalidTypeException.class)
	public void willThrowAnInvalidTypeExceptionIfGivenAnInterface() throws Exception {
		final Parameter aParameter = new Parameter("root.a", "");
		final Target<CharSequence> target = Target.create(CharSequence.class, "root");
		iogi.instantiate(target, aParameter);
	}
	
	@Test(expected=InvalidTypeException.class)
	public void willThrowAnInvalidTypeExceptionIfGivenAnAbstractClass() throws Exception {
		final Parameter aParameter = new Parameter("root.a", "");
		final Target<AbstractClass> target = Target.create(AbstractClass.class, "root");
		iogi.instantiate(target, aParameter);
	}
	
	@Test(expected=InvalidTypeException.class)
	public void willThrowAnInvalidTypeExceptionIfGivenVoid() throws Exception {
		final Parameter aParameter = new Parameter("root.a", "");
		final Target<Void> target = Target.create(Void.class, "root");
		iogi.instantiate(target, aParameter);
	}
	
	@Test
	public void emptyIntegerParametersWillBeInstantiatedAsZero() throws Exception {
		 final OneIntegerPrimitive object = iogi.instantiate(Target.create(OneIntegerPrimitive.class, "foo"), new Parameter("foo.anInteger", ""));
		 assertEquals(0, object.getAnInteger());
	}
	
	@Test
	public void emptyDoubleParametersWillBeInstantiatedAsZero() throws Exception {
		final OneDoublePrimitive object = iogi.instantiate(Target.create(OneDoublePrimitive.class, "foo"), new Parameter("foo.aDouble", ""));
		assertEquals(0d, object.getADouble(), 0.00d);
	}
	
	@Test
	public void canInstantiateAClassWithOnlyOneProtectedConstructor() throws Exception {
		final OnlyOneProtectedConstructor object = iogi.instantiate(Target.create(OnlyOneProtectedConstructor.class, "blah"));
		assertNotNull(object);
	}
	
	@Test
	public void willCallDependenciesProviderForConstructorParametersItCantInstantiate() throws Exception {
		final Parameter parameterForInstantiableArg = new Parameter("root.instantiable", "instantiable ok");
		final Target<HasDependency> target = Target.create(HasDependency.class, "root");
		
		final Iogi iogi = new Iogi(new DependencyProvider() {
			public Object provide(final Target<?> target) {
				return "uninstantiable ok";
			}

			public boolean canProvide(final Target<?> target) {
				return target.getName().equals("uninstantiable");
			}
		}, new DefaultLocaleProvider());
		
		final HasDependency object = iogi.instantiate(target, parameterForInstantiableArg);
		assertEquals("instantiable ok", object.getInstantiable());
		assertEquals("uninstantiable ok", object.getUninstantiable());
	}
	
	@Test
	public void willReturnNullIfThereAreNoParametersNorDependenciesAtLeastForOneFormalParameter() throws Exception {
		final Target<OneIntOneStringAndOneObject> target = Target.create(OneIntOneStringAndOneObject.class, "root");
		final OneIntOneStringAndOneObject object = iogi.instantiate(target, new Parameter("root.anObject.anInteger", "111"));
		
		assertNull(object);
	}
	
	public static class HasDependency {
		private final String instantiable;
		private final String uninstantiable;

		public HasDependency(final String instantiable, final String uninstantiable) {
			this.instantiable = instantiable;
			this.uninstantiable = uninstantiable;
		}
		
		public String getInstantiable() {
			return instantiable;
		}
		
		public String getUninstantiable() {
			return uninstantiable;
		}
	}

    private static class IogiWithUrlConverter implements Instantiator<Object> {
        private final MultiInstantiator allInstantiators;

        private IogiWithUrlConverter() {
            final List<Instantiator<?>> all = new ImmutableList.Builder<Instantiator<?>>()
                    .add(fallbackToNull(new UrlConverter()))
                    .add(new ArrayInstantiator(this))
                    .add(new ListInstantiator(this))
                    .add(new ObjectInstantiator(this, new NullDependencyProvider(), new ParanamerParameterNamesProvider()))
                    .build();

            this.allInstantiators = new MultiInstantiator(all);
        }

        public boolean isAbleToInstantiate(final Target<?> target) {
            return allInstantiators.isAbleToInstantiate(target);
        }

        public Object instantiate(final Target<?> target, final Parameters parameters) {
            return allInstantiators.instantiate(target, parameters);
        }
    }

    private static class UrlConverter extends TypeConverter<URL> {
        @Override
        protected URL convert(final String stringValue, final Target<?> to) throws Exception {
            return new URL(stringValue);
        }

        public boolean isAbleToInstantiate(final Target<?> target) {
            return target.getClassType().equals(URL.class);
        }
    }
}