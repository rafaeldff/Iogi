/**
 * 
 */
package br.com.caelum.iogi.fixtures;

public class OneStringOneConstructible {
	private final String one;
	private final OneIntegerPrimitive two;

	public OneStringOneConstructible(final String one,  final OneIntegerPrimitive two) {
		this.one = one;
		this.two = two;
	}

	public String getOne() {
		return one;
	}

	public OneIntegerPrimitive getTwo() {
		return two;
	}
}