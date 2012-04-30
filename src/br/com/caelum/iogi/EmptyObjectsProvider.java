package br.com.caelum.iogi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

public class EmptyObjectsProvider implements DependencyProvider {
   public static final Map<? extends Class<?>, ? extends Supplier<?>> JAVA_EMPTY_SUPPLIERS = ImmutableMap.of(List.class,
         new Supplier<List<Object>>() {
            public List<Object> get() {
               return new ArrayList<Object>();
            };
         });
   private final Map<? extends Class<?>, ? extends Supplier<?>> emptyInstances;
   private final DependencyProvider underlying;

   public EmptyObjectsProvider(DependencyProvider underlying, Map<? extends Class<?>, ? extends Supplier<?>> emptyInstances) {
      this.underlying = underlying;
      this.emptyInstances = emptyInstances;
   }

   @Override
   public boolean canProvide(Target<?> target) {
      return selfCanProvide(target) || underlying.canProvide(target);
   }

   private boolean selfCanProvide(Target<?> target) {
      return emptyInstances.containsKey(target.getClassType());
   }

   @Override
   public Object provide(Target<?> target) {
      return underlying.canProvide(target) ? underlying.provide(target) : emptyInstances.get(target.getClassType()).get();
   }

}
