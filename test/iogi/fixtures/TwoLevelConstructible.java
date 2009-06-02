/**
 * 
 */
package iogi.fixtures;

public class TwoLevelConstructible {
	private final OneConstructibleArgument level2;

	public TwoLevelConstructible(final OneConstructibleArgument level2) {
		this.level2 = level2;
	}
	
	public OneConstructibleArgument getLevel2() {
		return level2;
	}
}