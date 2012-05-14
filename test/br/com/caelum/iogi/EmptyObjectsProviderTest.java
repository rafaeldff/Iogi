package br.com.caelum.iogi;

import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
   
   @Test
   public void willDirectlyProvideEmptyArraysRegardlessOfTheReceivedSuppliers() throws Exception {
      assumeUnderlyingCannotProvideAnything();
      
      Target<NormalObject[]> normalObjectTarget = Target.create(NormalObject[].class, "");
      assertTrue(provider.canProvide(normalObjectTarget));
      
      Object theArray = provider.provide(normalObjectTarget);
      
      assertThat(theArray ,is(instanceOf(NormalObject[].class)));
      assertThat((Object[])theArray, is(emptyArray()));
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
   
   private EmptyObjectsProvider javaProvider = EmptyObjectsProvider.javaEmptyObjectsProvider(underlying);
   
   @SuppressWarnings("unchecked")
   @Test
   public void javaEmptyObjectsProviderCanProvideNewEmptyMutableSets() throws Exception {
      assumeUnderlyingCannotProvideAnything();
      Target<Set<Object>> setTarget = new Target<Set<Object>>(Set.class, "");
      
      assertTrue(javaProvider.canProvide(setTarget));
      
      Set<Object> firstSet = (Set<Object>) javaProvider.provide(setTarget);
      assertTrue(firstSet.isEmpty());
      
      Set<Object> secondSet = (Set<Object>)javaProvider.provide(setTarget);
      assertTrue(secondSet.isEmpty());
      
      assertNotSame(firstSet, secondSet);
      
      firstSet.add(new Object());
      assertEquals(1, firstSet.size());
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void javaEmptyObjectsProviderCanProvideNewEmptyMutableLists() throws Exception {
      assumeUnderlyingCannotProvideAnything();
      Target<List<Object>> listTarget = new Target<List<Object>>(List.class, "");

      assertTrue(javaProvider.canProvide(listTarget));

      List<Object> firstList = (List<Object>) javaProvider.provide(listTarget);
      assertTrue(firstList.isEmpty());

      List<Object> secondList = (List<Object>) javaProvider.provide(listTarget);
      assertTrue(secondList.isEmpty());

      assertNotSame(firstList, secondList);

      firstList.add(new Object());
      assertEquals(1, firstList.size());
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void javaEmptyObjectsProviderCanProvideNewEmptyMutableMaps() throws Exception {
      assumeUnderlyingCannotProvideAnything();
      Target<Map<Object, Object>> mapTarget = new Target<Map<Object,Object>>(Map.class, "");

      assertTrue(javaProvider.canProvide(mapTarget));

      Map<Object, Object> firstMap = (Map<Object, Object>) javaProvider.provide(mapTarget);
      assertTrue(firstMap.isEmpty());

      Map<Object, Object> secondMap = (Map<Object, Object>) javaProvider.provide(mapTarget);
      assertTrue(secondMap.isEmpty());

      assertNotSame(firstMap, secondMap);

      firstMap.put("key", "value");
      assertEquals(1, firstMap.size());
   }
   
   @Test
   public void javaEmptyObjectsProviderCanProvideNewObjectArrays() throws Exception {
      assumeUnderlyingCannotProvideAnything();
      Target<Object[]> arrayTarget = Target.create(Object[].class, "");

      assertTrue(javaProvider.canProvide(arrayTarget));

      Object[] array = (Object[]) javaProvider.provide(arrayTarget);
      assertThat(array, is(emptyArray()));
   }
   
   @Test
   public void javaEmptyObjectsProviderCanProvideListsOfCustomObjects() throws Exception {
      assumeUnderlyingCannotProvideAnything();
      Target<List<NormalObject>> listTarget = new Target<List<NormalObject>>(List.class, "");

      assertTrue(javaProvider.canProvide(listTarget));

      @SuppressWarnings("unchecked")
      List<NormalObject> list = (List<NormalObject>) javaProvider.provide(listTarget);
      assertNotNull(list);
   }
   
   @Test
   public void javaEmptyObjectsProviderCanProvideArraysOfCustomObjects() throws Exception {
      assumeUnderlyingCannotProvideAnything();
      Target<NormalObject[]> arrayTarget = Target.create(NormalObject[].class, "");

      assertTrue(javaProvider.canProvide(arrayTarget));

      NormalObject[] array = (NormalObject[]) javaProvider.provide(arrayTarget);
      assertThat(array, is(emptyArray()));
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
