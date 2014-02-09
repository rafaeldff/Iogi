package br.com.caelum.iogi.fixtures.generic;

public abstract class MyGenericClass<T extends Number> {
	abstract T getCode();
	abstract void setCode(T code);
}