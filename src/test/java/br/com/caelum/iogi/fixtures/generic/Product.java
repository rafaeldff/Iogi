package br.com.caelum.iogi.fixtures.generic;


public class Product extends MyGenericClass<Integer> implements MyGenericInterface<Integer> {

	private Integer id;
	private Integer code;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getCode() {
		return code;
	}
	
	public void setCode(Integer code) {
		this.code = code;
	}
}