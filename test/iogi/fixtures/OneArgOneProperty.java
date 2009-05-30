/**
 * 
 */
package iogi.fixtures;

public class OneArgOneProperty {
	private final Double oneArg;
	private int oneProperty;

	public OneArgOneProperty(Double oneArg) {
		this.oneArg = oneArg;
	}

	public int getOneProperty() {
		return oneProperty;
	}

	public void setOneProperty(int oneProperty) {
		this.oneProperty = oneProperty;
	}

	public Double getOneArg() {
		return oneArg;
	}
}