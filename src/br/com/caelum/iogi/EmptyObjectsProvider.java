package br.com.caelum.iogi;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class EmptyObjectsProvider implements DependencyProvider {
   public static EmptyObjectsProvider javaEmptyObjectsProvider(DependencyProvider underlying) {
      Map<? extends Class<?>, ? extends Supplier<?>> javaEmptySuppliers = ImmutableMap.<Class<?>, Supplier<?>>builder()
            .put(List.class, CollectionSuppliers.listSupplier())
            .put(Set.class, CollectionSuppliers.setSupplier())
            .put(Map.class, CollectionSuppliers.mapSupplier())
            .build();
      return new EmptyObjectsProvider(underlying, javaEmptySuppliers);
   }
   
   private final Map<? extends Class<?>, ? extends Supplier<?>> emptySuppliers;
   private final DependencyProvider underlying;

   public EmptyObjectsProvider(DependencyProvider underlying, Map<? extends Class<?>, ? extends Supplier<?>> emptyInstances) {
      this.underlying = underlying;
      this.emptySuppliers = emptyInstances;
   }

   public boolean canProvide(Target<?> target) {
      return target.getClassType().isArray() || selfCanProvide(target) || underlying.canProvide(target);
   }

   private boolean selfCanProvide(Target<?> target) {
      Class<?> targetType = target.getClassType();
      return emptySuppliers.containsKey(targetType);
   }

   public Object provide(Target<?> target) {
      if (target.getClassType().isArray()) {
         return emptyArrayFor(target);
      }
      if (underlying.canProvide(target)) {
         return underlying.provide(target);
      }
      return emptySuppliers.get(target.getClassType()).get();
   }

   private Object emptyArrayFor(Target<?> target) {
      return Array.newInstance(target.arrayElementType(), 0);
   }

   private static class CollectionSuppliers {
      public static Supplier<List<Object>> listSupplier() { 
         return new Supplier<List<Object>>() {
            public List<Object> get() {
               return Lists.newArrayList();
            };
         };
      }
      
      public static Supplier<Set<Object>> setSupplier() { 
         return new Supplier<Set<Object>>() {
            public Set<Object> get() {
               return Sets.newHashSet();
            };
         };
      }
    
      public static Supplier<Map<Object,Object>> mapSupplier() {
         return new Supplier<Map<Object,Object>>() {
            public Map<Object, Object> get() {
               return Maps.newHashMap();
            }
         };
      }
   }

}

