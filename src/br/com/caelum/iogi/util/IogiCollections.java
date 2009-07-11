package br.com.caelum.iogi.util;

import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Maps;

public class IogiCollections {
	private IogiCollections() {}

	public static <A, B> Iterator<Entry<A, B>> zip(final Iterator<A> first, final Iterator<B> second) {
		return new AbstractIterator<Entry<A,B>>(){
			@Override
			protected Entry<A, B> computeNext() {
				if (!first.hasNext() || !second.hasNext()) {
					return endOfData();
				}
				
				return Maps.immutableEntry(first.next(), second.next());
			}
		};
	}
}
