/**
 * 
 */
package iogi;

import iogi.fixtures.OneString;

public class MixedObjectAndArray {
	private final OneString[] array;
	private final OneString object;

	public MixedObjectAndArray(final OneString[] array, final OneString object) {
		this.array = array;
		this.object = object;
	}

	public OneString[] getArray() {
		return array;
	}

	public OneString getObject() {
		return object;
	}
}