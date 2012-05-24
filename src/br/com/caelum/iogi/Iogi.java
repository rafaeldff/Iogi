package br.com.caelum.iogi;

import static br.com.caelum.iogi.EmptyObjectsProvider.javaEmptyObjectsProvider;
import static br.com.caelum.iogi.conversion.FallbackConverter.fallbackTo;
import static br.com.caelum.iogi.conversion.FallbackConverter.fallbackToNull;

import java.util.Arrays;
import java.util.List;

import br.com.caelum.iogi.collections.ArrayInstantiator;
import br.com.caelum.iogi.collections.ListInstantiator;
import br.com.caelum.iogi.conversion.BigDecimalConverter;
import br.com.caelum.iogi.conversion.BigIntegerConverter;
import br.com.caelum.iogi.conversion.BooleanPrimitiveConverter;
import br.com.caelum.iogi.conversion.BooleanWrapperConverter;
import br.com.caelum.iogi.conversion.BytePrimitiveConverter;
import br.com.caelum.iogi.conversion.ByteWrapperConverter;
import br.com.caelum.iogi.conversion.CharacterPrimitiveConverter;
import br.com.caelum.iogi.conversion.CharacterWrapperConverter;
import br.com.caelum.iogi.conversion.DoublePrimitiveConverter;
import br.com.caelum.iogi.conversion.DoubleWrapperConverter;
import br.com.caelum.iogi.conversion.EnumConverter;
import br.com.caelum.iogi.conversion.FloatPrimitiveConverter;
import br.com.caelum.iogi.conversion.FloatWrapperConverter;
import br.com.caelum.iogi.conversion.IntegerPrimitiveConverter;
import br.com.caelum.iogi.conversion.IntegerWrapperConverter;
import br.com.caelum.iogi.conversion.LocaleBasedCalendarConverter;
import br.com.caelum.iogi.conversion.LongPrimitiveConverter;
import br.com.caelum.iogi.conversion.LongWrapperConverter;
import br.com.caelum.iogi.conversion.ShortPrimitiveConverter;
import br.com.caelum.iogi.conversion.ShortWrapperConverter;
import br.com.caelum.iogi.conversion.StringConverter;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.ParanamerParameterNamesProvider;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.iogi.spi.LocaleProvider;

import com.google.common.collect.ImmutableList;

public class Iogi {
	private final MultiInstantiator allInstantiators;
	
	public Iogi(final DependencyProvider dependencyProvider, final LocaleProvider localeProvider) {
		final List<Instantiator<?>>  all = new ImmutableList.Builder<Instantiator<?>>()
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
			.add(fallbackToNull(new LocaleBasedCalendarConverter(localeProvider)))
			.add(fallbackTo(new BooleanPrimitiveConverter(), false))
			.add(fallbackTo(new BytePrimitiveConverter(), (byte)0))
			.add(fallbackTo(new CharacterPrimitiveConverter(), (char)0))
			.add(fallbackTo(new DoublePrimitiveConverter(), 0d))
			.add(fallbackTo(new FloatPrimitiveConverter(), 0f))
			.add(fallbackTo(new IntegerPrimitiveConverter(), 0))
			.add(fallbackTo(new LongPrimitiveConverter(), 0l))
			.add(fallbackTo(new ShortPrimitiveConverter(), (short)0))
			.add(new ArrayInstantiator(new DelegateToAllInstantatiors()))
			.add(new ListInstantiator(new DelegateToAllInstantatiors()))
			.add(new ObjectInstantiator(new DelegateToAllInstantatiors(), javaEmptyObjectsProvider(dependencyProvider), new ParanamerParameterNamesProvider()))
			.build();
	
		this.allInstantiators = new MultiInstantiator(all);
	}
	
	public <T> T instantiate(final Target<T> target, final Parameter... parameters) {
		return instantiate(target, new Parameters(Arrays.asList(parameters)));
	}
	
	public <T> T instantiate(final Target<T> target, final Parameters parameters) {
		final Object object = allInstantiators.instantiate(target, parameters);
		return target.cast(object);
	}
	
	/*
	 *	This is an ugly hack to enable cyclic references between allInstantiators
	 * 	and some of its components - such as objectInstantiator - that require a 
	 * 	recursive reference to allInstantiators.
	 */
	private final class DelegateToAllInstantatiors implements Instantiator<Object> {
		public boolean isAbleToInstantiate(final Target<?> target, Parameters parameters) {
			return allInstantiators.isAbleToInstantiate(target, parameters);
		}

		public Object instantiate(final Target<?> target, final Parameters parameters) {
			return allInstantiators.instantiate(target, parameters);
		}
	}
}
