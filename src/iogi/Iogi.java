package iogi;

import iogi.conversion.DoubleConverter;
import iogi.conversion.Instantiator;
import iogi.conversion.IntegerConverter;
import iogi.conversion.StringConverter;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class Iogi {
	private List<Instantiator<?>>  all = new ImmutableList.Builder<Instantiator<?>>()
		.add(new IntegerConverter())
		.add(new DoubleConverter())
		.add(new StringConverter())
		.add(new DelegateToListInstantiator())
		.add(new DelegateToObjectInstantiator())
		.build();
	private MultiInstantiator multiInstantiator = new MultiInstantiator(all);
	private ListInstantiator listInstantiator = new ListInstantiator(multiInstantiator);
	private ObjectInstantiator objectInstantiator = new ObjectInstantiator(multiInstantiator);
	

	public <T> T instantiate(Target<T> target, Parameter... parameters) {
		return instantiate(target, new Parameters(Arrays.asList(parameters)));
	}
	
	public <T> T instantiate(Target<T> target, Parameters parameters) {
		Object object = multiInstantiator.instantiate(target, parameters);
		return target.cast(object);
	}
	
	/*
	 *	This is an ugly hack to enable cyclic references between MultiInstantiator and
	 *  ListInstantiator.
	 */
	private final class DelegateToListInstantiator implements Instantiator<List<Object>> {
		@Override
		public List<Object> instantiate(Target<?> target, Parameters parameters) {
			return listInstantiator.instantiate(target, parameters);
		}

		@Override
		public boolean isAbleToInstantiate(Target<?> target) {
			return listInstantiator.isAbleToInstantiate(target);
		}
	}
	
	/*
	 *	This is an ugly hack to enable cyclic references between MultiInstantiator and
	 *  ObjectInstantiator.
	 */
	private final class DelegateToObjectInstantiator implements Instantiator<Object> {
		@Override
		public Object instantiate(Target<?> target, Parameters parameters) {
			return objectInstantiator.instantiate(target, parameters);
		}
		
		@Override
		public boolean isAbleToInstantiate(Target<?> target) {
			return objectInstantiator.isAbleToInstantiate(target);
		}
	}
}
