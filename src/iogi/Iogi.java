package iogi;

import static iogi.conversion.FallbackConverter.fallback;
import iogi.conversion.BigDecimalConverter;
import iogi.conversion.BigIntegerConverter;
import iogi.conversion.BooleanWrapperConverter;
import iogi.conversion.ByteWrapperConverter;
import iogi.conversion.CharacterWrapperConverter;
import iogi.conversion.DoubleWrapperConverter;
import iogi.conversion.IntegerWrapperConverter;
import iogi.conversion.StringConverter;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class Iogi {
	private List<Instantiator<?>>  all = new ImmutableList.Builder<Instantiator<?>>()
		.add(fallback(new BigDecimalConverter(), BigDecimal.ZERO))
		.add(fallback(new BigIntegerConverter(), BigInteger.ZERO))
		.add(fallback(new BooleanWrapperConverter(), false))
		.add(fallback(new ByteWrapperConverter(), (byte)0))
		.add(fallback(new CharacterWrapperConverter(), (char)0))
		.add(fallback(new IntegerWrapperConverter(), 0))
		.add(fallback(new DoubleWrapperConverter(), 0d))
		.add(fallback(new StringConverter(), null))
		.add(new ListInstantiator(new DelegateToAllInstantatiors()))
		.add(new ObjectInstantiator(new DelegateToAllInstantatiors()))
		.build();
	
	private MultiInstantiator allInstantiators = new MultiInstantiator(all);
	
	public <T> T instantiate(Target<T> target, Parameter... parameters) {
		return instantiate(target, new Parameters(Arrays.asList(parameters)));
	}
	
	public <T> T instantiate(Target<T> target, Parameters parameters) {
		Object object = allInstantiators.instantiate(target, parameters);
		return target.cast(object);
	}
	
	/*
	 *	This is an ugly hack to enable cyclic references between allInstantiators
	 * 	and some of its components - like objectInstantiator - that require a 
	 * 	recursive reference to allInstantiators.
	 */
	private final class DelegateToAllInstantatiors implements Instantiator<Object> {
		@Override
		public boolean isAbleToInstantiate(Target<?> target) {
			return allInstantiators.isAbleToInstantiate(target);
		}

		@Override
		public Object instantiate(Target<?> target, Parameters parameters) {
			return allInstantiators.instantiate(target, parameters);
		}
	}
}
