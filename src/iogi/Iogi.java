package iogi;

import static iogi.conversion.FallbackConverter.fallbackTo;
import static iogi.conversion.FallbackConverter.fallbackToNull;
import iogi.conversion.BigDecimalConverter;
import iogi.conversion.BigIntegerConverter;
import iogi.conversion.BooleanPrimitiveConverter;
import iogi.conversion.BooleanWrapperConverter;
import iogi.conversion.BytePrimitiveConverter;
import iogi.conversion.ByteWrapperConverter;
import iogi.conversion.CharacterPrimitiveConverter;
import iogi.conversion.CharacterWrapperConverter;
import iogi.conversion.DoublePrimitiveConverter;
import iogi.conversion.DoubleWrapperConverter;
import iogi.conversion.EnumConverter;
import iogi.conversion.FloatPrimitiveConverter;
import iogi.conversion.FloatWrapperConverter;
import iogi.conversion.IntegerPrimitiveConverter;
import iogi.conversion.IntegerWrapperConverter;
import iogi.conversion.LongPrimitiveConverter;
import iogi.conversion.LongWrapperConverter;
import iogi.conversion.ShortPrimitiveConverter;
import iogi.conversion.ShortWrapperConverter;
import iogi.conversion.StringConverter;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class Iogi {
	private List<Instantiator<?>>  all = new ImmutableList.Builder<Instantiator<?>>()
		.add(fallbackToNull(new BigDecimalConverter()))
		.add(fallbackToNull(new BigIntegerConverter()))
		.add(fallbackToNull(new BooleanWrapperConverter()))
		.add(fallbackToNull(new ByteWrapperConverter()))
		.add(fallbackToNull(new CharacterWrapperConverter()))
		.add(fallbackToNull(new DoubleWrapperConverter()))
		.add(fallbackToNull(new IntegerWrapperConverter()))
		.add(fallbackToNull(new EnumConverter()))
		.add(fallbackToNull(new FloatWrapperConverter()))
		.add(fallbackToNull(new IntegerWrapperConverter()))
		.add(fallbackToNull(new LongWrapperConverter()))
		.add(fallbackToNull(new ShortWrapperConverter()))
		.add(fallbackToNull(new StringConverter()))
		.add(fallbackTo(new BooleanPrimitiveConverter(), false))
		.add(fallbackTo(new BytePrimitiveConverter(), (byte)0))
		.add(fallbackTo(new CharacterPrimitiveConverter(), (char)0))
		.add(fallbackTo(new DoublePrimitiveConverter(), 0d))
		.add(fallbackTo(new FloatPrimitiveConverter(), 0f))
		.add(fallbackTo(new IntegerPrimitiveConverter(), 0))
		.add(fallbackTo(new LongPrimitiveConverter(), 0l))
		.add(fallbackTo(new ShortPrimitiveConverter(), (short)0))
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
