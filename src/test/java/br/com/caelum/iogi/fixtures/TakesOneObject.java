package br.com.caelum.iogi.fixtures;

public class TakesOneObject {


	private final OneIntegerPrimitive objectWithInteger;

	public TakesOneObject(OneIntegerPrimitive objectWithInteger) {
		this.objectWithInteger = objectWithInteger;
	}
	
	public OneIntegerPrimitive getObjectWithInteger() {
		return objectWithInteger;
	}
}
