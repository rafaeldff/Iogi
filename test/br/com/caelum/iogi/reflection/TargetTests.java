package br.com.caelum.iogi.reflection;

import br.com.caelum.iogi.DependenciesInjector;
import br.com.caelum.iogi.fixtures.OnlyOneProtectedConstructor;
import br.com.caelum.iogi.spi.ParameterNamesProvider;

import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.AccessibleObject;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TargetTests {
   private ParameterNamesProvider nullNamesProvider = new ParameterNamesProvider() {
      public List<String> lookupParameterNames(AccessibleObject methodOrConstructor) {
         return Collections.emptyList();
      }
   };

   @Test
   public void willListProtectedConstructors() {
      final Target<OnlyOneProtectedConstructor> target = Target.create(OnlyOneProtectedConstructor.class, "foo");
      final Constructors classConstructors = target.constructors(nullNamesProvider,
            DependenciesInjector.nullDependenciesInjector());
      assertEquals(1, classConstructors.size());
   }
   
   @Test @Ignore
   public void willNotListSyntheticConstructors() throws Exception {
     Target<?> target = Target.create(Class.forName("WithSyntheticConstructor"), "");
     Constructors constructors = target.constructors(nullNamesProvider, DependenciesInjector.nullDependenciesInjector());
     assertEquals(0, constructors.size());
   }
}
