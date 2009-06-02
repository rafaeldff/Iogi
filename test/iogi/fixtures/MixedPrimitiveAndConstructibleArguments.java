/**
 * 
 */
package iogi.fixtures;

public class MixedPrimitiveAndConstructibleArguments {
	private final int one;
	private final OneIntegerPrimitive two;

	public MixedPrimitiveAndConstructibleArguments(final int one,  final OneIntegerPrimitive two) {
		this.one = one;
		this.two = two;
	}

	public int getOne() {
		return one;
	}

	public OneIntegerPrimitive getTwo() {
		return two;
	}
}