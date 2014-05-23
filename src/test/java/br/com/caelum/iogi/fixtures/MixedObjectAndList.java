/**
 * 
 */
package br.com.caelum.iogi.fixtures;


import java.util.List;

public class MixedObjectAndList {
	private final List<OneString> list;
	private final OneString object;

	public MixedObjectAndList(final List<OneString> list, final OneString object) {
		this.list = list;
		this.object = object;
	}
	
	public List<OneString> getList() {
		return list;
	}
	
	public OneString getObject() {
		return object;
	}
}