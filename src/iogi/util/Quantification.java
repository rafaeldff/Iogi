package iogi.util;

import static com.google.common.base.Predicates.not;

import java.util.Collection;

import com.google.common.base.Predicate;

public class Quantification {
	public static <E> boolean exists(Collection<E> collection, Predicate<E> elementPredicate) {
		return Predicates.exists(elementPredicate).apply(collection);
	}
	
	public static <E> boolean forAll(Collection<E> collection, Predicate<E> elementPredicate) {
		return Predicates.forAll(elementPredicate).apply(collection);
	}
	
	public static class Predicates {
		public static <E> Predicate<Collection<E>> exists(final Predicate<E> elementPredicate) {
			return new Predicate<Collection<E>>() {
				public boolean apply(Collection<E> input) {
					for (E element : input) {
						if (elementPredicate.apply(element))
							return true;
					}
					return false;
				}
			};
		}
	
		public static <E> Predicate<Collection<E>> forAll(final Predicate<E> elementPredicate) {
			return not(exists(not(elementPredicate)));
		}
	}
}
