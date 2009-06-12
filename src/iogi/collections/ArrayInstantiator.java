package iogi.collections;

import iogi.Instantiator;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.lang.reflect.Array;


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
		final ParametersByIndex parametersByIndex = new ParametersByIndex(parameters.relevantTo(target), target);
	
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
			
			for (int i : parametersByIndex.indexes()) {
				Array.set(array, i, instantiateArrayElement(i));
			}
			
			return array;
		}

		private Object makeArray() {
			int arrayLength = parametersByIndex.isEmpty() ? 0 : parametersByIndex.highestIndex() + 1;
			return Array.newInstance(arrayTarget.arrayElementType(), arrayLength);
		}
		
		private Object instantiateArrayElement(final int index) {
			final Target<?> elementTarget = arrayTarget.arrayElementTarget();
			return elementInstantiator.instantiate(elementTarget, parametersByIndex.get(index));
		}
	}
}
