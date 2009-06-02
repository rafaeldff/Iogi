/**
 * 
 */
package iogi.fixtures;

public class OneConstructibleArgument {
	private final OneIntegerPrimitive arg;

	public OneConstructibleArgument(final OneIntegerPrimitive arg) {
		this.arg = arg;
	}
	
	public OneIntegerPrimitive getArg() {
		return arg;
	}
}