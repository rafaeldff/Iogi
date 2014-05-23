package br.com.caelum.iogi.fixtures.generic;

public interface MyGenericInterface<T extends Number> {
	T getId();
	void setId(T id);
}