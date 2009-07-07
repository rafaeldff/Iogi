/**
 * 
 */
package br.com.caelum.iogi.fixtures;

public class TwoConstructibleArguments {
	private final OneString one;
	private final OneIntegerPrimitive two;

	public TwoConstructibleArguments(final OneString one, final OneIntegerPrimitive two) {
		this.one = one;
		this.two = two;
	}

	public OneString getOne() {
		return one;
	}

	public OneIntegerPrimitive getTwo() {
		return two;
	}
}