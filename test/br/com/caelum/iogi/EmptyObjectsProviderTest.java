package br.com.caelum.iogi;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import br.com.caelum.iogi.conversion.StringConverter;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.ParanamerParameterNamesProvider;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.hamcrest.Matchers.instanceOf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EmptyObjectsProviderTest {
   private Mockery context = new Mockery();
   private DependencyProvider underlying = context.mock(DependencyProvider.class); 
   
   private Class<?> classThatMayBeEmpty = MayBeEmpty.class;
   private Supplier<MayBeEmpty> emptyValueSupplier = new Supplier<MayBeEmpty>() {
      @Override
      public MayBeEmpty get() {
         return new MayBeEmpty();
      }
   };
   private Target<?> targetForEmptyObject = Target.create(classThatMayBeEmpty, "");
   private EmptyObjectsProvider provider = 
      new EmptyObjectsProvider(underlying, ImmutableMap.of(classThatMayBeEmpty, emptyValueSupplier));
   
   public void assumeUnderlyingCannotProvideAnything() {
      context.checking(new Expectations() {{
         allowing(underlying).canProvide(with(any(Target.class))); will(returnValue(false));
      }});
   }
   
   @After
   public void tearDown() {
      context.assertIsSatisfied();
   }
   
   @Test
   public void willProvideAnEmptyObjectWhenOneIsRequired() throws Exception {
      assumeUnderlyingCannotProvideAnything();
      assertTrue(provider.canProvide(targetForEmptyObject));
      Assert.assertThat(provider.provide(targetForEmptyObject), instanceOf(MayBeEmpty.class));
   }
   
   @Test
   public void willNotProvideObjectsThatHaveNoMappingNorAreProvidableByTheUnderlyingProvider() throws Exception {
      assumeUnderlyingCannotProvideAnything();
      final Target<NormalObject> target = Target.create(NormalObject.class, "");
      assertFalse(provider.canProvide(target));
   }
   
   @Test
   public void objectsProvidedForAGivenTargetWillNotBeTheSame() throws Exception {
      assumeUnderlyingCannotProvideAnything();
      assertTrue(provider.canProvide(targetForEmptyObject));
      Object first = provider.provide(targetForEmptyObject);
      Object second = provider.provide(targetForEmptyObject);
      
      Assert.assertNotSame(first, second);
   }
   
   @Test
   public void willDelegateWhenTheUnderlyingProviderIsAbleToProvideForTheTarget() throws Exception {
      final Target<?> target = Target.create(DifferentObject.class, "");
      final Object provided = new DifferentObject();
      context.checking(new Expectations() {{
         oneOf(underlying).canProvide(target); will(returnValue(true));
         oneOf(underlying).provide(target); will(returnValue(provided));
      }});
      
      Assert.assertSame(provided, provider.provide(target));
   }
   
   @Test
   public void ifUnderlyingIsAbleThenWillDelegateEvenIfCanProvideEmptyInstance() throws Exception {
      final Target<?> target = Target.create(MayBeEmpty.class, "");
      final Object provided = new MayBeEmpty();
      context.checking(new Expectations() {{
         oneOf(underlying).canProvide(target); will(returnValue(true));
         oneOf(underlying).provide(target); will(returnValue(provided));
      }});
      
      Assert.assertSame(provided, provider.provide(target));
   }
   
   private Instantiator<Object> instantiator = new MultiInstantiator(
         ImmutableList.of(
               new StringConverter(),
               new ObjectInstantiator(new RecursiveInstantiator(), provider, new ParanamerParameterNamesProvider())));
   private final class RecursiveInstantiator implements Instantiator<Object> {
      @Override
      public boolean isAbleToInstantiate(Target<?> target) {
         return instantiator.isAbleToInstantiate(target);
      }

      @Override
      public Object instantiate(Target<?> target, Parameters parameters) {
         return instantiator.instantiate(target, parameters);
      }
   }
   
   @Test
   public void canBeUsedToInstantiateAnObject() throws Exception {
      assumeUnderlyingCannotProvideAnything();
      Target<DependsOnEmptyAndNormal> target = Target.create(DependsOnEmptyAndNormal.class, "root");
      Parameters parameters = new Parameters(new Parameter("root.normal.parameter", "foo"));
      DependsOnEmptyAndNormal object = (DependsOnEmptyAndNormal) instantiator.instantiate(target, parameters);
      assertEquals("foo", object.normal.parameter);
      assertThat(object.mayBeEmpty, instanceOf(MayBeEmpty.class));
   }

   static class NormalObject {
      private final String parameter;

      public NormalObject(String parameter) {
         this.parameter = parameter;
      }
   }

   static class MayBeEmpty {
   }
   
   static class DependsOnEmptyAndNormal {
      final NormalObject normal;
      final MayBeEmpty mayBeEmpty;

      public DependsOnEmptyAndNormal(MayBeEmpty mayBeEmpty, NormalObject normal) {
         this.mayBeEmpty = mayBeEmpty;
         this.normal = normal;
      }
   }
   
   static class DifferentObject {
      
   }

}
