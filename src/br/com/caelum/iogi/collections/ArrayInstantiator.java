package br.com.caelum.iogi.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.TreeSet;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;


public class ArrayInstantiator implements Instantiator<Object> {
	private final Instantiator<Object> elementInstantiator;

	public ArrayInstantiator(final Instantiator<Object> elementInstantiator) {
		this.elementInstantiator = elementInstantiator;
	}
	
	@Override
	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType().isArray();
	}
	
	@Override
	public Object instantiate(final Target<?> target, final Parameters parameters) {
		final ParametersByIndex parametersByIndex = new ParametersByIndex(parameters, target);
	
		final ArrayFactory factory = new ArrayFactory(target, parametersByIndex);
		
		return factory.getArray();
	}
	
	private class ArrayFactory {
		private final ParametersByIndex parametersByIndex;
		private final Target<?> arrayTarget;

		public ArrayFactory(final Target<?> target, final ParametersByIndex parametersByIndex) {
			this.arrayTarget = target;
			this.parametersByIndex = parametersByIndex;
		}

		public Object getArray() {
			final Object array = makeArray();
			
			int indexIntoTheArray = 0;
			final Collection<Integer> orderedIndexes = new TreeSet<Integer>(parametersByIndex.indexes());
			for (final int indexOfTheParameters : orderedIndexes) {
				final Parameters parameters = parametersByIndex.at(indexOfTheParameters);
				Array.set(array, indexIntoTheArray++, instantiateArrayElement(parameters));
			}
			
			return array;
		}

		private Object makeArray() {
			final int arrayLength = parametersByIndex.count();
			return Array.newInstance(arrayTarget.arrayElementType(), arrayLength);
		}
		
		private Object instantiateArrayElement(final Parameters parameters) {
			final Target<?> elementTarget = arrayTarget.arrayElementTarget();
			return elementInstantiator.instantiate(elementTarget, parameters);
		}
	}
}
