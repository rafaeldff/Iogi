package iogi.fixtures;

import java.util.List;

public class OneGenericListProperty {
	private List<OneIntegerPrimitive> list;
	
	public void setList(final List<OneIntegerPrimitive> list) {
		this.list = list;
	}
	
	public List<OneIntegerPrimitive> getList() {
		return list;
	}
}
