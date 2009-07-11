package br.com.caelum.iogi.fixtures;

public class OneIntOneStringAndOneObject {
	private final String aString;
	private final int anInt;
	private final OneIntegerPrimitive anObject;
	
	public OneIntOneStringAndOneObject(final String aString, final int anInt, final OneIntegerPrimitive anObject) {
		this.aString = aString;
		this.anInt = anInt;
		this.anObject = anObject;
	}

	public String getaString() {
		return aString;
	}

	public int getAnInt() {
		return anInt;
	}

	public OneIntegerPrimitive getAnObject() {
		return anObject;
	}
}
