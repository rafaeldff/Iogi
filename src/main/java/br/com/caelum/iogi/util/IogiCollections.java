package br.com.caelum.iogi.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map.Entry;

public class IogiCollections {
    private IogiCollections() {
    }

    public static <A, B> Iterable<Entry<A, B>> zip(final Iterable<A> firstIterable, final Iterable<B> secondIterable) {
        return new Iterable<Entry<A, B>>() {
            public Iterator<Entry<A, B>> iterator() {
                return new AbstractIterator<Entry<A, B>>() {
                    private Iterator<A> first = firstIterable.iterator();
                    private Iterator<B> second = secondIterable.iterator();

                    @Override
                    protected Entry<A, B> computeNext() {
                        if (!first.hasNext() || !second.hasNext()) {
                            return endOfData();
                        }

                        return Maps.immutableEntry(first.next(), second.next());
                    }
                };
            }
        };
    }
}
